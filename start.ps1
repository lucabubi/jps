#!/usr/bin/env pwsh

# Strict error handling
$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

param(
    [switch]$DryRun
)

# Respect DRY_RUN environment variable too
if (-not $DryRun) {
    if ($env:DRY_RUN -and ($env:DRY_RUN -in @('1','true','True'))) {
        $DryRun = $true
    }
}
if ($DryRun) { Write-Host "[INFO] DRY RUN mode enabled — commands will not be executed." }

# Resolve script root and work from there
$ScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Definition
Set-Location -Path $ScriptRoot
Write-Host "[INFO] Script root: $ScriptRoot"

function Gradle-Build {
    param(
        [Parameter(Mandatory=$true)][string]$Dir,
        [Parameter(Mandatory=$true)][string]$Image
    )

    Write-Host "[BUILD] $Dir -> $Image"
    if (-not (Test-Path $Dir)) {
        Write-Warning "Directory not found: $Dir"
        return
    }

    Push-Location $Dir
    try {
        # Use --no-daemon to mirror start.sh behaviour
        if ($DryRun) {
            Write-Host "[DRYRUN] ./gradlew --no-daemon bootBuildImage --imageName=$Image"
        } else {
            & ./gradlew --no-daemon bootBuildImage --imageName=$Image
        }
    } finally {
        Pop-Location
    }
}

# Build backend images
$builds = @(
    @{ dir = 'gatewayAPI'; image = 'g19/gateway_api' },
    @{ dir = 'CRM'; image = 'g19/crm' },
    @{ dir = 'document_store'; image = 'g19/document_store' },
    @{ dir = 'communication_manager'; image = 'g19/communication_manager' },
    @{ dir = 'analytics_crm'; image = 'g19/analytics_crm' }
)

foreach ($b in $builds) {
    Gradle-Build -Dir $b.dir -Image $b.image
}

# Frontend build
$frontendDir = 'user-interface/JobPlacementServices'
if (Test-Path $frontendDir) {
    Write-Host "[BUILD] $frontendDir -> g19/user-interface"
    Push-Location $frontendDir
    try {
        if ($DryRun) {
            Write-Host "[DRYRUN] docker build -t g19/user-interface ."
        } else {
            & docker build -t g19/user-interface .
        }
    } finally {
        Pop-Location
    }
} else {
    Write-Warning "Frontend directory not found: $frontendDir"
}

# Ensure network exists
$networkName = 'jps-net'
$networkExists = $false
if ($DryRun) {
    Write-Host "[DRYRUN] Skipping docker network inspection/creation; assuming network exists"
    $networkExists = $true
} else {
    try {
        & docker network inspect $networkName > $null 2>&1
        $networkExists = $true
    } catch {
        $networkExists = $false
    }
}

if (-not $networkExists) {
    Write-Host "[NET] Creating network $networkName"
    if ($DryRun) {
        Write-Host "[DRYRUN] docker network create $networkName"
    } else {
        & docker network create $networkName
    }
}

# Compose files (mirrors start.sh)
$composeFiles = @(
    'gatewayAPI/compose_mac.yaml',
    'CRM/compose.yaml',
    'analytics_crm/compose.yaml',
    'communication_manager/compose.yaml',
    'document_store/compose.yaml',
    'user-interface/JobPlacementServices/compose.yaml'
)

# Build compose argument array and warn if files missing
$composeArgs = @()
foreach ($f in $composeFiles) {
    if (-not (Test-Path $f)) {
        Write-Warning "Compose file missing: $f"
    }
    $composeArgs += '-f'
    $composeArgs += $f
}

Write-Host "[UP] Avvio stack Docker..."

$dockerCmd = 'docker'
$dockerArgs = @('compose') + $composeArgs + @('up','-d')
Write-Host "[CMD] $dockerCmd $($dockerArgs -join ' ')"
if ($DryRun) {
    Write-Host "[DRYRUN] Skipping docker compose up"
} else {
    & $dockerCmd $dockerArgs
}

Write-Host "[DONE] Build e avvio completati."
