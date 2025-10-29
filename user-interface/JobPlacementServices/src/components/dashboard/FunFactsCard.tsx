'use client'

import { useEffect, useMemo, useState, useCallback, useRef } from 'react'

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import funFacts from '@/assets/funfacts.json'

// JSON type
type FunFact = { title: string; description: string }
const facts = funFacts as unknown as FunFact[]

const FunFactsCard = () => {
    // Opacity for fade effect
    const [fade, setFade] = useState(1)
    const fadeOutRef = useRef<number | null>(null)
    const fadeInRef = useRef<number | null>(null)
    const [index, setIndex] = useState(() => {
        const len = facts?.length ?? 0
        return len > 0 ? Math.floor(Math.random() * len) : 0
    })

    const fact = useMemo<FunFact>(() => {
        if (!Array.isArray(facts) || facts.length === 0) {
            return { title: 'Fun fact', description: 'No facts available.' }
        }
        return facts[index] ?? facts[0]
    }, [index])

    const showNext = useCallback(() => {
        const len = facts?.length ?? 0
        if (len <= 1) return
        // delete previous animations
        if (fadeOutRef.current) window.clearTimeout(fadeOutRef.current)
        if (fadeInRef.current) window.clearTimeout(fadeInRef.current)

        // select new index, if possible
        let next = index
        while (next === index && len > 1) {
            next = Math.floor(Math.random() * len)
        }

        // sequence: fade-out -> change content -> fade-in
        setFade(0)
        fadeOutRef.current = window.setTimeout(() => {
            setIndex(next)
            // piccolo delay per assicurare l'aggiornamento del DOM prima del fade-in
            fadeInRef.current = window.setTimeout(() => setFade(1), 20)
        }, 200) // durata in ms del fade-out
    }, [index])

    // cleanup on unmount
    useEffect(() => {
        return () => {
            if (fadeOutRef.current) window.clearTimeout(fadeOutRef.current)
            if (fadeInRef.current) window.clearTimeout(fadeInRef.current)
        }
    }, [])

    useEffect(() => {
        const all = document.querySelectorAll('.spotlight-card')

        const handleMouseMove = (ev: MouseEvent) => {
            all.forEach(e => {
                const blob = e.querySelector('.blob') as HTMLElement
                const fblob = e.querySelector('.fake-blob') as HTMLElement

                if (!blob || !fblob) return

                const rec = fblob.getBoundingClientRect()

                blob.style.opacity = '1'

                blob.animate(
                    [
                        {
                            transform: `translate(${
                                ev.clientX - rec.left - rec.width / 2
                            }px, ${ev.clientY - rec.top - rec.height / 2}px)`
                        }
                    ],
                    {
                        duration: 300,
                        fill: 'forwards'
                    }
                )
            })
        }

        window.addEventListener('mousemove', handleMouseMove)

        return () => {
            window.removeEventListener('mousemove', handleMouseMove)
        }
    }, [])

    return (
        <div className='h-max w-full'>
            <div
                className='spotlight-card group bg-border relative overflow-hidden rounded-xl p-px transition-all duration-300 ease-in-out cursor-pointer'
                onClick={showNext}
                title='Click to show another fun fact'
                aria-label='Show another fun fact'
            >
                <Card
                    className='group-hover:bg-card/90 w-full border-none transition-all duration-300 ease-in-out group-hover:backdrop-blur-[20px] select-none'
                    onKeyDown={(e) => {
                        if (e.key === 'Enter' || e.key === ' ') {
                            e.preventDefault()
                            showNext()
                        }
                    }}
                    role='button'
                    tabIndex={0}
                    aria-label='Show another fun fact (keyboard)'
                >
                    <div className='transition-opacity duration-200 ease-in-out' style={{ opacity: fade }}>
                        <CardHeader className="!pt-3.5 !pb-3.5">
                            <CardTitle>{fact.title}</CardTitle>
                        </CardHeader>
                        <CardContent className="!pb-3.5">
                            {fact.description}
                        </CardContent>
                    </div>
                </Card>
                <div className='blob pointer-events-none absolute top-0 left-0 h-20 w-20 rounded-full bg-violet-600/60 opacity-0 blur-2xl transition-all duration-300 ease-in-out dark:bg-violet-500/60' />
                <div className='fake-blob pointer-events-none absolute top-0 left-0 h-20 w-20 rounded-full' />
             </div>
         </div>
     )
 }

 export default FunFactsCard
