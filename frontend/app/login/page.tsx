"use client"

import type React from "react"

import { useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { motion, AnimatePresence } from "framer-motion"
import { useAppDispatch } from "@/lib/hooks"
import { setUser } from "@/lib/store"
import { Phone, Shield, Truck, Star, Award, ArrowRight } from "lucide-react"
import { useGoogleLogin } from "@react-oauth/google"
import { authService } from "@/lib/services/auth-service"
import { OtpModal } from "@/components/otp-modal"

export default function LoginPage() {
  const router = useRouter()
  const dispatch = useAppDispatch()

  const [mobile, setMobile] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState("")
  const [showOtpModal, setShowOtpModal] = useState(false)



  const handleGoogleLogin = useGoogleLogin({
    onSuccess: async (tokenResponse) => {
      setIsLoading(true)
      try {
        const userInfoResponse = await fetch("https://www.googleapis.com/oauth2/v3/userinfo", {
          headers: { Authorization: `Bearer ${tokenResponse.access_token}` },
        })
        const userInfo = await userInfoResponse.json()

        const tempUser = {
          name: userInfo.name,
          email: userInfo.email,
          image: userInfo.picture,
          googleId: userInfo.sub
        }

        localStorage.setItem("temp_google_user", JSON.stringify(tempUser))
        router.push("/register")
      } catch (error) {
        console.error("Google login failed", error)
        setError("Failed to login with Google")
      } finally {
        setIsLoading(false)
      }
    },
    onError: () => {
      setError("Google login failed")
      setIsLoading(false)
    },
  })

  const handleSendOtp = async (e?: React.FormEvent) => {
    e?.preventDefault()
    if (mobile.length !== 10) {
      setError("Please enter a valid 10-digit mobile number")
      return
    }

    setIsLoading(true)
    setError("")

    try {
      // Using sendQuickOtp as the primary login method now
      const response = await authService.sendQuickOtp(mobile)

      if (response.data) {
        setShowOtpModal(true)
      } else {
        setError(response.data || "Failed to send OTP")
      }
    } catch (err) {
      console.error("Send OTP error:", err)
      setError("Failed to send OTP. Please try again.")
    } finally {
      setIsLoading(false)
    }
  }

  const handleLoginSuccess = () => {
    const userStr = localStorage.getItem("tyreplus_user")
    if (userStr) {
      const user = JSON.parse(userStr)
      dispatch(setUser(user))
    }
    router.push("/")
  }



  const benefits = [
    { icon: <Shield className="w-6 h-6" />, title: "Secure Payments", desc: "100% safe & secure transactions" },
    { icon: <Truck className="w-6 h-6" />, title: "Free Installation", desc: "At your doorstep, pan India" },
    { icon: <Star className="w-6 h-6" />, title: "Quality Assured", desc: "Only verified tyres" },
    { icon: <Award className="w-6 h-6" />, title: "Best Prices", desc: "Guaranteed lowest prices" },
  ]

  return (
    <div className="min-h-screen bg-[#F9FAFB] flex">
      {/* Left Side - Login Form */}
      <div className="flex-1 flex items-center justify-center p-6 md:p-12">
        <div className="w-full max-w-md">
          <div className="text-center mb-8">
            <Link href="/" className="inline-flex items-center gap-2 mb-6">
              <div className="w-12 h-12 rounded-xl flex items-center justify-center overflow-hidden">
                <img src="/otb-logo.png" alt="OTB" className="w-full h-full object-cover" />
              </div>
              <span className="text-2xl font-bold text-[#1F2937]">Online Tyre Bazaar</span>
            </Link>
            <h1 className="text-2xl md:text-3xl font-bold text-[#1F2937] mb-2">
              Welcome Back! 👋
            </h1>
            <p className="text-[#6B7280]">
              Sign in using your mobile number
            </p>
          </div>

          <div className="bg-white rounded-2xl shadow-[0_4px_20px_rgba(0,0,0,0.08)] p-8">
            {error && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                className="mb-4 p-3 bg-[#FEF2F2] border border-[#FECACA] rounded-lg text-[#DC2626] text-sm"
              >
                {error}
              </motion.div>
            )}


            {/* Google Sign In */}
            <button
              type="button"
              onClick={() => handleGoogleLogin()}
              className="w-full py-3 bg-white border border-[#D1D5DB] text-[#1F2937] rounded-xl font-semibold hover:bg-[#F9FAFB] transition-colors flex items-center justify-center gap-2 mb-6"
            >
              <svg className="w-5 h-5" viewBox="0 0 24 24">
                <path
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                  fill="#4285F4"
                />
                <path
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                  fill="#34A853"
                />
                <path
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"
                  fill="#FBBC05"
                />
                <path
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"
                  fill="#EA4335"
                />
              </svg>
              Sign in with Google
            </button>

            <div className="relative mb-6">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-[#E5E7EB]" />
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-4 bg-white text-[#9CA3AF]">Or continue with mobile</span>
              </div>
            </div>

            <form onSubmit={handleSendOtp}>
              <div className="mb-6">
                <label className="block text-sm font-medium text-[#1F2937] mb-2">Mobile Number</label>
                <div className="relative">
                  <Phone className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-[#9CA3AF]" />
                  <span className="absolute left-12 top-1/2 -translate-y-1/2 text-[#6B7280]">+91</span>
                  <input
                    type="tel"
                    value={mobile}
                    onChange={(e) => {
                      const val = e.target.value.replace(/\D/g, "").slice(0, 10)
                      setMobile(val)
                      setError("")
                    }}
                    placeholder="Enter mobile number"
                    className="w-full pl-20 pr-4 py-3 border border-[#D1D5DB] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0D9488] focus:border-transparent transition-all"
                    autoFocus
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={isLoading || mobile.length !== 10}
                className={`w-full py-3 rounded-xl font-semibold flex items-center justify-center gap-2 transition-all ${mobile.length === 10 && !isLoading
                  ? "bg-gradient-to-r from-[#14B8A6] to-[#0D9488] text-white hover:opacity-90"
                  : "bg-[#E5E7EB] text-[#9CA3AF] cursor-not-allowed"
                  }`}
              >
                {isLoading ? (
                  <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                ) : (
                  <>
                    Send OTP
                    <ArrowRight className="w-5 h-5" />
                  </>
                )}
              </button>
            </form>

            {/* Register Link */}
            <div className="relative my-6">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-[#E5E7EB]" />
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-4 bg-white text-[#9CA3AF]">New to Online Tyre Bazaar?</span>
              </div>
            </div>

            <Link
              href="/register"
              className="block w-full py-3 text-center border-2 border-[#0D9488] text-[#0D9488] rounded-xl font-semibold hover:bg-[#F0FDFA] transition-colors"
            >
              Create an Account
            </Link>
          </div>
        </div>
      </div>

      {/* Right Side - Benefits */}
      <div className="hidden lg:flex flex-1 bg-gradient-to-br from-[#14B8A6] to-[#0D9488] p-12 items-center justify-center">
        <div className="max-w-md">
          <h2 className="text-3xl font-bold text-white mb-4">Why Online Tyre Bazaar? 🛞</h2>
          <p className="text-white/80 mb-8">Join thousands of happy customers who trust us for their tyre needs.</p>

          <div className="space-y-6">
            {benefits.map((benefit, index) => (
              <motion.div
                key={benefit.title}
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.3 + index * 0.1 }}
                className="flex items-start gap-4"
              >
                <div className="w-12 h-12 bg-white/20 rounded-xl flex items-center justify-center flex-shrink-0 text-white">
                  {benefit.icon}
                </div>
                <div>
                  <h3 className="text-lg font-semibold text-white">{benefit.title}</h3>
                  <p className="text-white/70">{benefit.desc}</p>
                </div>
              </motion.div>
            ))}
          </div>

          <div className="mt-12 p-6 bg-white/10 rounded-2xl backdrop-blur-sm">
            <div className="flex items-center gap-4 mb-4">
              <div className="flex -space-x-2">
                {[1, 2, 3, 4].map((i) => (
                  <div key={i} className="w-8 h-8 bg-white/30 rounded-full border-2 border-white/50" />
                ))}
              </div>
              <span className="text-white font-semibold">50,000+ Happy Customers</span>
            </div>
            <p className="text-white/80 text-sm italic">
              "Best tyre shopping experience! Free installation saved me so much hassle." - Rahul M.
            </p>
          </div>
        </div>
      </div>
      <OtpModal
        isOpen={showOtpModal}
        onClose={() => setShowOtpModal(false)}
        initialPhone={mobile}
        onSuccess={handleLoginSuccess}
      />
    </div>
  )
}
