"use client"

import { Suspense } from "react"
import { useSearchParams, useRouter } from "next/navigation"
import { useEffect, useState } from "react"
import Link from "next/link"
import Image from "next/image"
import { tyreData } from "@/lib/tyre-data"
import { CheckCircle2, ClipboardList, Coins, SearchCheck, CalendarCheck, ChevronRight } from "lucide-react"
import { orderService } from "@/lib/services/order-service"

function QuoteContent() {
    const searchParams = useSearchParams()
    const router = useRouter()
    const tyreId = searchParams.get("tyreId")
    const [tyre, setTyre] = useState<(typeof tyreData)[0] | null>(null)

    useEffect(() => {
        if (tyreId) {
            const foundTyre = tyreData.find((t) => t.id === tyreId)
            if (foundTyre) {
                setTyre(foundTyre)
            } else {
                // Handle invalid ID if needed, or just redirect back
                // router.push("/search")
            }
        }
    }, [tyreId])

    if (!tyre) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-[#FFF1F2]">
                <div className="animate-pulse flex flex-col items-center">
                    <div className="h-8 w-32 bg-gray-200 rounded mb-4"></div>
                    <div className="h-4 w-48 bg-gray-200 rounded"></div>
                </div>
            </div>
        )
    }

    const [isSubmitting, setIsSubmitting] = useState(false)
    const [isSuccess, setIsSuccess] = useState(false)

    const handleConfirm = async () => {
        if (!tyre) return
        setIsSubmitting(true)
        try {
            // Create request object
            const requestData = {
                tyreName: `${tyre.brand} ${tyre.pattern} ${tyre.size}`,
                vehicle: "Unknown", // user vehicle if available
                status: "Open"
            }
            await orderService.createRequest(requestData)
            setIsSuccess(true)
        } catch (error) {
            console.error("Failed to submit request", error)
            // Show error toast
        } finally {
            setIsSubmitting(false)
        }
    }

    if (isSuccess) {
        return (
            <div className="min-h-screen bg-[#F0FDFA] flex items-center justify-center p-4">
                <div className="bg-white rounded-2xl shadow-xl p-8 max-w-md w-full text-center">
                    <div className="w-20 h-20 bg-[#D1FAE5] rounded-full flex items-center justify-center mx-auto mb-6">
                        <CheckCircle2 className="w-10 h-10 text-[#059669]" />
                    </div>
                    <h2 className="text-2xl font-bold text-[#1F2937] mb-2">Request Submitted!</h2>
                    <p className="text-gray-600 mb-8">
                        Your request for <strong>{tyre?.brand} {tyre?.pattern}</strong> has been sent to verified dealers. Expect quotes shortly!
                    </p>
                    <Link
                        href="/my-orders"
                        className="block w-full py-3 bg-[#0D9488] text-white rounded-xl font-semibold hover:bg-[#0F766E] transition-colors"
                    >
                        View My Requests
                    </Link>
                    <Link
                        href="/search"
                        className="block w-full py-3 mt-3 text-[#6B7280] font-medium hover:text-[#1F2937]"
                    >
                        Back to Search
                    </Link>
                </div>
            </div>
        )
    }

    // ... (rest of the component for Review state)

    return (
        <div className="min-h-screen bg-[#F9FAFB]">
            {/* Navbar Placeholder */}

            <main className="max-w-3xl mx-auto px-4 py-8">
                {/* Page Title */}
                <div className="mb-6">
                    <h1 className="text-2xl font-bold text-[#1F2937] flex items-center gap-2">
                        <ClipboardList className="w-6 h-6 text-[#8B5CF6]" />
                        REVIEW & CONFIRM
                    </h1>
                    <p className="text-sm text-gray-600 ml-8">Review details before requesting quotes</p>
                </div>

                {/* Product Card */}
                <div className="bg-white rounded-2xl p-6 shadow-sm mb-6 flex flex-col sm:flex-row items-center gap-6 border border-gray-100">
                    <div className="relative w-32 h-32 flex-shrink-0">
                        <Image
                            src={tyre.image || "/placeholder.svg"}
                            alt={`${tyre.brand} ${tyre.pattern}`}
                            fill
                            className="object-contain"
                        />
                    </div>
                    <div className="flex-1 text-center sm:text-left">
                        <h2 className="text-xl font-bold text-[#1F2937] mb-2">
                            {tyre.brand} {tyre.pattern} {tyre.size}
                        </h2>
                        <div className="flex items-center justify-center sm:justify-start gap-3">
                            <span className="text-2xl font-bold text-[#0D9488]">
                                ₹{tyre.price.toLocaleString()}
                            </span>
                            {/* ... discount ... */}
                        </div>
                    </div>
                </div>

                {/* Action Section */}
                <div className="bg-white rounded-2xl p-8 shadow-sm text-center">
                    <h3 className="text-lg font-semibold text-[#1F2937] mb-4">
                        Ready to get quotes from dealers?
                    </h3>
                    <p className="text-gray-500 mb-8 max-w-lg mx-auto">
                        By clicking confirm, we will send this request to top rated dealers in your area. You will receive competitive quotes within 30 minutes.
                    </p>

                    <button
                        onClick={handleConfirm}
                        disabled={isSubmitting}
                        className="w-full sm:w-auto px-8 py-3 bg-gradient-to-r from-[#14B8A6] to-[#0D9488] text-white rounded-xl font-bold hover:shadow-lg hover:shadow-teal-500/30 transition-all flex items-center justify-center gap-2 mx-auto disabled:opacity-70"
                    >
                        {isSubmitting ? (
                            <>Processing...</>
                        ) : (
                            <>
                                Confirm Request <ChevronRight className="w-5 h-5" />
                            </>
                        )}
                    </button>
                </div>

                {/* Steps Section (Keep existing as info) */}
                {/* ... existing steps ... */}
            </main>
        </div>
    )
}

export default function QuotePage() {
    return (
        <Suspense fallback={
            <div className="min-h-screen flex items-center justify-center bg-[#FFF1F2]">
                <div className="animate-pulse flex flex-col items-center">
                    <div className="h-8 w-32 bg-gray-200 rounded mb-4"></div>
                    <div className="h-4 w-48 bg-gray-200 rounded"></div>
                </div>
            </div>
        }>
            <QuoteContent />
        </Suspense>
    )
}
