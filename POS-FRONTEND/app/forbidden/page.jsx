"use client";
import { useRouter } from "next/navigation";
import { ErrorPageLayout, PrimaryButton, SecondaryButton } from "@/components/errorPages/ErrorPageShell";

function IconLock() {
    return (
        <svg width="40" height="40" viewBox="0 0 24 24" fill="none"
            stroke="#dc2626" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <rect x="3" y="11" width="18" height="11" rx="2" />
            <path d="M7 11V7a5 5 0 0 1 10 0v4" />
        </svg>
    );
}

export default function ForbiddenPage() {
    const router = useRouter();
    return (
        <ErrorPageLayout
            errorCode="403"
            title="Access Forbidden"
            message={<>You don&apos;t have permission to view this page. If you think this is a mistake, contact your administrator.</>}
            icon={<IconLock />}
            actions={<>
                <PrimaryButton onClick={() => router.push("/home")}>Go to Home</PrimaryButton>
                <SecondaryButton onClick={() => router.back()}>Go Back</SecondaryButton>
            </>}
        />
    );
}