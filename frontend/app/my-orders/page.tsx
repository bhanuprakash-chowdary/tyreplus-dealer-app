"use client"

import { useState, useEffect } from "react"
import { OrderCard } from "@/components/order-card"
import { RequestCard } from "@/components/request-card"
import { orderService, type Request } from "@/lib/services/order-service"
import { type Order } from "@/types/order"
import { LayoutDashboard, CheckCircle2 } from "lucide-react"

export default function MyOrdersPage() {
    const [orders, setOrders] = useState<Order[]>([])
    const [requests, setRequests] = useState<Request[]>([])
    const [isLoading, setIsLoading] = useState(true)

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [ordersRes, requestsRes] = await Promise.all([
                    orderService.getOrders(),
                    orderService.getRequests()
                ])
                setOrders(ordersRes.data)
                setRequests(requestsRes.data)
            } catch (error) {
                console.error("Failed to fetch dashboard data:", error)
            } finally {
                setIsLoading(false)
            }
        }
        fetchData()
    }, [])

    // Calculate stats
    const activeRequests = requests.filter(r => r.status === "Active").length
    const completedOrders = orders.filter(o => o.status === "delivered").length
    const totalSaved = 12500 // Mock calculation for now

    return (
        <div className="container mx-auto px-4 py-10">
            <main className="w-full">
                <div className="mb-8 text-center">
                    <h1 className="text-3xl font-bold flex items-center justify-center gap-2">
                        <LayoutDashboard className="w-8 h-8 text-gray-600" />
                        MY DASHBOARD
                    </h1>
                    <p className="text-muted-foreground mt-2">
                        Track and manage all your tyre requests
                    </p>
                </div>

                {/* Stats Cards */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
                    <div className="bg-white p-6 rounded-xl shadow-sm border flex items-center gap-4">
                        <div className="w-12 h-12 rounded-full bg-[#F0FDFA] flex items-center justify-center text-2xl">
                            🎯
                        </div>
                        <div>
                            <p className="text-sm font-semibold text-gray-500">Active Requests</p>
                            <p className="text-3xl font-bold text-[#0D9488]">{activeRequests}</p>
                        </div>
                    </div>
                    <div className="bg-white p-6 rounded-xl shadow-sm border flex items-center gap-4">
                        <div className="w-12 h-12 rounded-full bg-[#F0FDFA] flex items-center justify-center">
                            <CheckCircle2 className="w-8 h-8 text-[#0D9488]" />
                        </div>
                        <div>
                            <p className="text-sm font-semibold text-gray-500">Completed</p>
                            <p className="text-3xl font-bold text-[#0D9488]">{completedOrders}</p>
                        </div>
                    </div>
                    <div className="bg-white p-6 rounded-xl shadow-sm border flex items-center gap-4">
                        <div className="w-12 h-12 rounded-full bg-[#F0FDFA] flex items-center justify-center text-2xl">
                            💰
                        </div>
                        <div>
                            <p className="text-sm font-semibold text-gray-500">Total Saved</p>
                            <p className="text-3xl font-bold text-[#0D9488]">₹{totalSaved.toLocaleString()}</p>
                        </div>
                    </div>
                </div>

                {/* Recent Orders */}
                <section className="mb-10">
                    <h2 className="text-xl font-bold text-[#1F2937] mb-6">Recent Orders</h2>
                    <div className="space-y-6">
                        {isLoading ? (
                            [...Array(2)].map((_, i) => (
                                <div key={i} className="bg-white rounded-xl p-6 border border-gray-200 h-[150px] animate-pulse"></div>
                            ))
                        ) : (
                            orders.map((order) => (
                                <OrderCard key={order.id} order={order} />
                            ))
                        )}
                    </div>
                </section>

                {/* Recent Requests */}
                <section>
                    <h2 className="text-xl font-bold text-[#1F2937] mb-6">Recent Requests</h2>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                        {isLoading ? (
                            [...Array(2)].map((_, i) => (
                                <div key={i} className="bg-white rounded-xl p-6 border border-gray-200 h-[200px] animate-pulse"></div>
                            ))
                        ) : requests.length > 0 ? (
                            requests.map((request) => (
                                <RequestCard key={request.id} request={request} />
                            ))
                        ) : (
                            <div className="text-center py-12 border rounded-lg bg-muted/20 col-span-full">
                                <h3 className="text-lg font-medium">No requests found</h3>
                                <p className="text-muted-foreground">You haven't made any requests yet.</p>
                            </div>
                        )}
                    </div>
                </section>
            </main>
        </div>
    )
}
