import { Banner, BannerTitle, BannerClose } from '@/components/ui/shadcn-io/banner/banner.tsx';

export default function LandingBanner() {
    return (
        <Banner defaultVisible={true} inset >
            <BannerTitle>Announcement of official launch date released: LIVE on October 8, 2025!</BannerTitle>
            <BannerClose />
        </Banner>
    );
}