import { useEffect } from "react";
import { useIsAppleDevice } from "@/hooks/useIsAppleDevice";

type HotkeyHandlers = Partial<Record<string, (e: KeyboardEvent) => void>>;

interface UseHotkeysOptions {
    requireMod?: boolean; // true: requires ⌘/Ctrl
}

export default function useHotkeys(handlers: HotkeyHandlers, options?: UseHotkeysOptions) {
    const isApple = useIsAppleDevice();
    const requireMod = options?.requireMod ?? true;

    useEffect(() => {
        const onKeyDown = (e: KeyboardEvent) => {
            if (requireMod) {
                const mod = isApple ? e.metaKey : e.ctrlKey;
                if (!mod) return;
            }
            const key = e.key.toLowerCase();
            const handler = handlers[key];
            if (handler) {
                e.preventDefault();
                handler(e);
            }
        };

        window.addEventListener("keydown", onKeyDown);
        return () => window.removeEventListener("keydown", onKeyDown);
        // Callbacks memoization is required to avoid re-adding the event listener on every render
    }, [handlers, isApple, requireMod]);
}