import { useCallback, useEffect, useState } from "react";
import { toast } from "sonner";

export default function useFullscreen() {
  const [isFullscreen, setIsFullscreen] = useState<boolean>(false);

  const handleChange = useCallback(() => {
    setIsFullscreen(!!document.fullscreenElement);
  }, []);

  useEffect(() => {
    document.addEventListener("fullscreenchange", handleChange);
    return () => {
      document.removeEventListener("fullscreenchange", handleChange);
    };
  }, [handleChange]);

  const toggleFullscreen = useCallback(() => {
    if (!document.fullscreenElement) {
      document.documentElement.requestFullscreen();
        toast.success("Entered fullscreen mode.");
    } else {
      document.exitFullscreen();
        toast.success("Exited fullscreen mode.");
    }
  }, []);

  return { isFullscreen, toggleFullscreen };
}

