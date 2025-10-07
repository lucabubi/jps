import { useEffect, useState } from "react";

export function useIsAppleDevice() {
    const [isApple, setIsApple] = useState(false);

    useEffect(() => {
        const isMac = /(Mac|iPhone|iPod|iPad)/i.test(navigator.userAgent);
        setIsApple(isMac);
    }, []);

    return isApple;
}