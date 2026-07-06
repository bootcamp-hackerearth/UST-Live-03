"use client";
import { useRouter } from "next/navigation";
import { ErrorPageLayout, PrimaryButton, SecondaryButton, C } from "@/components/errorPages/ErrorPageShell";

function IconCompass() {
    return (
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none"
            stroke={C.mid} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"
            aria-hidden="true" focusable="false">
            <circle cx="12" cy="12" r="10" />
            <polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76" />
        </svg>
    );
}

export default function NotFoundPage() {
    const router = useRouter();
    return (
        <ErrorPageLayout
            errorCode="404"
            title="Page Not Found"
            message={<>The page you&apos;re looking for doesn&apos;t exist or may have been moved.</>}
            icon={<IconCompass />}
            iconDanger={false}
            actions={<>
                <PrimaryButton onClick={() => router.push("/home")}>Go to Home</PrimaryButton>
                <SecondaryButton onClick={() => router.back()}>Go Back</SecondaryButton>
            </>}
        />
    );
}