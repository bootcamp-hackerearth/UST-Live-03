"use client";
import { useRouter } from "next/navigation";
import { ErrorPageLayout, PrimaryButton, SecondaryButton } from "@/components/errorPages/ErrorPageShell";

function IconServer() {
    return (
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none"
            stroke="#dc2626" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <rect x="2" y="3" width="20" height="7" rx="1.5" />
            <rect x="2" y="14" width="20" height="7" rx="1.5" />
            <line x1="6" y1="6.5" x2="6.01" y2="6.5" />
            <line x1="6" y1="17.5" x2="6.01" y2="17.5" />
        </svg>
    );
}

export default function ServerErrorPage() {
    const router = useRouter();
    return (
        <ErrorPageLayout
            errorCode="500"
            title="Something Went Wrong"
            message={<>Our server ran into an unexpected problem. Please try again in a moment — if this keeps happening, contact support.</>}
            icon={<IconServer />}
            actions={<>
                <PrimaryButton onClick={() => globalThis.location.reload()}>Try Again</PrimaryButton>
                <SecondaryButton onClick={() => router.push("/home")}>Go to Home</SecondaryButton>
            </>}
        />
    );
}