"use client"

import type React from "react"

import { useState, useRef, useEffect } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { motion, AnimatePresence } from "framer-motion"
import { useAppDispatch } from "@/lib/hooks"
import { setUser } from "@/lib/store"
import { User, Phone, MapPin, ArrowRight, Check, AlertCircle } from "lucide-react"
import { useGoogleLogin } from "@react-oauth/google"
import { fetchLocationDetails } from "@/lib/geocode"
import { authService } from "@/lib/services/auth-service"

export default function RegisterPage() {
  const router = useRouter()
  const dispatch = useAppDispatch()

  const [step, setStep] = useState<"details" | "otp" | "success">("details")
  const [name, setName] = useState("")
  const [mobile, setMobile] = useState("")
  const [pincode, setPincode] = useState("")
  const [agreedToTerms, setAgreedToTerms] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [isGoogleAuth, setIsGoogleAuth] = useState(false)
  const [errors, setErrors] = useState<Record<string, string>>({})

  // OTP
  const [otp, setOtp] = useState(["", "", "", ""])
  const [timer, setTimer] = useState(30)
  const otpRefs = useRef<(HTMLInputElement | null)[]>([])

  useEffect(() => {
    if (step === "otp" && timer > 0) {
      const interval = setInterval(() => setTimer((t) => t - 1), 1000)
      return () => clearInterval(interval)
    }
  }, [step, timer])

  // Pincode Data
  const [city, setCity] = useState("")
  const [state, setState] = useState("")
  const [country, setCountry] = useState("")
  const [district, setDistrict] = useState("")
  const [isPincodeLoading, setIsPincodeLoading] = useState(false)

  useEffect(() => {
    if (pincode.length === 6) {
      const fetchPincodeDetails = async () => {
        setIsPincodeLoading(true)
        try {
          const details = await fetchLocationDetails(pincode)

          if (details) {
            setCity(details.city)
            setState(details.state)
            setCountry(details.country)
            setDistrict(details.district)
            setErrors((prev) => {
              const newErrors = { ...prev };
              delete newErrors.pincode; // Clear pincode error if valid
              return newErrors;
            })
          } else {
            setErrors((prev) => ({ ...prev, pincode: "Invalid pincode or not found" }))
            setCity("")
            setState("")
            setCountry("")
            setDistrict("")
          }
        } catch (error) {
          console.error("Failed to fetch pincode details", error)
          setErrors((prev) => ({ ...prev, pincode: "Failed to fetch pincode details" }))
        } finally {
          setIsPincodeLoading(false)
        }
      }
      fetchPincodeDetails()
    } else {
      setCity("")
      setState("")
      setCountry("")
      setDistrict("")
    }
  }, [pincode])

  // Check for temp Google user from Login page
  useEffect(() => {
    const tempUserStr = localStorage.getItem("temp_google_user")
    if (tempUserStr) {
      try {
        const tempUser = JSON.parse(tempUserStr)
        setName(tempUser.name)
        setIsGoogleAuth(true)
        // Clear it so it doesn't persist if they leave
        localStorage.removeItem("temp_google_user")
      } catch (e) {
        console.error("Failed to parse temp google user", e)
      }
    }
  }, [])

  const validateForm = () => {
    const newErrors: Record<string, string> = {}

    if (name.trim().length < 2) {
      newErrors.name = "Name must be at least 2 characters"
    }
    if (mobile.length !== 10) {
      newErrors.mobile = "Enter a valid 10-digit mobile number"
    }
    if (pincode.length !== 6) {
      newErrors.pincode = "Enter a valid 6-digit pincode"
    }
    if (!agreedToTerms) {
      newErrors.terms = "You must agree to the terms"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmitDetails = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validateForm()) return

    setIsLoading(true)
    try {
      await authService.sendRegisterOtp(mobile)
      setIsLoading(false)
      setStep("otp")
      setTimer(30)
      setTimeout(() => otpRefs.current[0]?.focus(), 100)
    } catch (error) {
      console.error("Failed to send OTP", error)
      setIsLoading(false)
    }
  }

  const handleOtpChange = (index: number, value: string) => {
    if (!/^\d*$/.test(value)) return
    const newOtp = [...otp]
    newOtp[index] = value.slice(-1)
    setOtp(newOtp)
    if (value && index < 5) {
      otpRefs.current[index + 1]?.focus()
    }
  }

  const handleOtpKeyDown = (index: number, e: React.KeyboardEvent) => {
    if (e.key === "Backspace" && !otp[index] && index > 0) {
      otpRefs.current[index - 1]?.focus()
    }
  }

  const handleVerifyOtp = async () => {
    const otpValue = otp.join("")
    if (otpValue.length !== 4) return
    setIsLoading(true)

    try {
      const registrationData = {
        name,
        mobile,
        pincode,
        city,
        state,
        country,
        district,
        otp: otpValue
      }

      const response = await authService.completeRegistration(registrationData)

      // Check for token as success indicator
      if (response.data.token) {
        // Backend UserInfo doesn't have mobile, inject it from state
        const user = {
          ...response.data.user,
          mobile: mobile
        }

        localStorage.setItem("tyreplus_user", JSON.stringify(user))
        localStorage.setItem("auth_token", response.data.token)
        dispatch(setUser(user))

        setIsLoading(false)
        setStep("success")

        setTimeout(() => {
          router.push("/")
        }, 2000)
      } else {
        console.error("Registration failed:", response.data)
        setIsLoading(false)
      }
    } catch (error) {
      console.error("Registration error", error)
      setIsLoading(false)
    }
  }

  const handleResendOtp = async () => {
    if (timer > 0) return
    setIsLoading(true)
    await new Promise((r) => setTimeout(r, 1000))
    setIsLoading(false)
    setTimer(30)
    setOtp(["", "", "", "", "", ""])
    otpRefs.current[0]?.focus()
  }

  const handleGoogleSignup = useGoogleLogin({
    onSuccess: async (tokenResponse) => {
      setIsLoading(true)
      try {
        const userInfoResponse = await fetch("https://www.googleapis.com/oauth2/v3/userinfo", {
          headers: { Authorization: `Bearer ${tokenResponse.access_token}` },
        })
        const userInfo = await userInfoResponse.json()

        setName(userInfo.name)
        setIsGoogleAuth(true)
      } catch (error) {
        console.error("Google signup failed", error)
      } finally {
        setIsLoading(false)
      }
    },
    onError: () => {
      setIsLoading(false)
    },
  })

  return (
    <div className="min-h-screen bg-[#F9FAFB] flex items-center justify-center p-6">
      <motion.div initial={{ opacity: 0, y: 20 }} animate={{ opacity: 1, y: 0 }} className="w-full max-w-lg">
        {/* Header */}
        <div className="text-center mb-8">
          <Link href="/" className="inline-flex items-center gap-2 mb-6">
            <div className="w-12 h-12 rounded-xl flex items-center justify-center overflow-hidden">
              <img src="/otb-logo.png" alt="OTB" className="w-full h-full object-cover" />
            </div>
            <span className="text-2xl font-bold text-[#1F2937]">Online Tyre Bazaar</span>
          </Link>
          <h1 className="text-2xl md:text-3xl font-bold text-[#1F2937] mb-2">
            {step === "details" && "Create Account 🚀"}
            {step === "otp" && "Verify Mobile 📱"}
            {step === "success" && "Welcome Aboard! 🎉"}
          </h1>
          <p className="text-[#6B7280]">
            {step === "details" && "Join thousands of happy customers"}
            {step === "otp" && `OTP sent to +91 ${mobile}`}
            {step === "success" && "Your account has been created"}
          </p>
        </div>

        {/* Progress Steps */}
        <div className="flex items-center justify-center gap-4 mb-8">
          {["Details", "Verify", "Done"].map((label, index) => {
            const stepIndex = step === "details" ? 0 : step === "otp" ? 1 : 2
            const isActive = index <= stepIndex
            const isCompleted = index < stepIndex

            return (
              <div key={label} className="flex items-center gap-2">
                <div
                  className={`w-8 h-8 rounded-full flex items-center justify-center text-sm font-semibold transition-all ${isCompleted
                    ? "bg-[#10B981] text-white"
                    : isActive
                      ? "bg-gradient-to-r from-[#14B8A6] to-[#0D9488] text-white"
                      : "bg-[#E5E7EB] text-[#9CA3AF]"
                    }`}
                >
                  {isCompleted ? <Check className="w-4 h-4" /> : index + 1}
                </div>
                <span className={`text-sm ${isActive ? "text-[#1F2937] font-medium" : "text-[#9CA3AF]"}`}>{label}</span>
                {index < 2 && <div className={`w-8 h-0.5 ${isActive ? "bg-[#0D9488]" : "bg-[#E5E7EB]"}`} />}
              </div>
            )
          })}
        </div>

        {/* Form Card */}
        <div className="bg-white rounded-2xl shadow-[0_4px_20px_rgba(0,0,0,0.08)] p-8">
          <AnimatePresence mode="wait">
            {step === "details" && (
              <motion.form
                key="details"
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: 20 }}
                onSubmit={handleSubmitDetails}
                className="space-y-4"
              >
                {/* Google Sign Up */}
                {!isGoogleAuth && (
                  <button
                    type="button"
                    onClick={() => handleGoogleSignup()}
                    className="w-full py-3 bg-white border border-[#D1D5DB] text-[#1F2937] rounded-xl font-semibold hover:bg-[#F9FAFB] transition-colors flex items-center justify-center gap-2 mb-4"
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
                    Sign up with Google
                  </button>
                )}

                {isGoogleAuth && (
                  <div className="bg-[#F0FDFA] border border-[#CCFBF1] rounded-xl p-4 mb-4 flex items-center gap-3">
                    <div className="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-sm">
                      <svg className="w-6 h-6" viewBox="0 0 24 24">
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
                    </div>
                    <div>
                      <p className="text-sm font-medium text-[#0D9488]">Signed in with Google</p>
                      <p className="text-xs text-[#0F766E]">Please complete your profile details</p>
                    </div>
                  </div>
                )}

                {/* Name */}
                <div>
                  <label className="block text-sm font-medium text-[#1F2937] mb-2">Full Name</label>
                  <div className="relative">
                    <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-[#9CA3AF]" />
                    <input
                      type="text"
                      value={name}
                      onChange={(e) => setName(e.target.value)}
                      placeholder="Enter your full name"
                      className={`w-full pl-12 pr-4 py-3 border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0D9488] focus:border-transparent transition-all ${errors.name ? "border-[#0D9488]" : "border-[#D1D5DB]"
                        }`}
                    />
                  </div>
                  {errors.name && (
                    <p className="text-sm text-[#0D9488] mt-1 flex items-center gap-1">
                      <AlertCircle className="w-4 h-4" />
                      {errors.name}
                    </p>
                  )}
                </div>

                {/* Mobile */}
                <div>
                  <label className="block text-sm font-medium text-[#1F2937] mb-2">Mobile Number</label>
                  <div className="relative">
                    <Phone className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-[#9CA3AF]" />
                    <span className="absolute left-12 top-1/2 -translate-y-1/2 text-[#6B7280]">+91</span>
                    <input
                      type="tel"
                      value={mobile}
                      onChange={(e) => setMobile(e.target.value.replace(/\D/g, "").slice(0, 10))}
                      placeholder="Enter mobile number"
                      className={`w-full pl-20 pr-4 py-3 border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0D9488] focus:border-transparent transition-all ${errors.mobile ? "border-[#0D9488]" : "border-[#D1D5DB]"
                        }`}
                    />
                  </div>
                  {errors.mobile && (
                    <p className="text-sm text-[#0D9488] mt-1 flex items-center gap-1">
                      <AlertCircle className="w-4 h-4" />
                      {errors.mobile}
                    </p>
                  )}
                </div>

                {/* Pincode */}
                <div>
                  <label className="block text-sm font-medium text-[#1F2937] mb-2">PIN Code 📍</label>
                  <div className="relative">
                    <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-[#9CA3AF]" />
                    <input
                      type="text"
                      value={pincode}
                      onChange={(e) => setPincode(e.target.value.replace(/\D/g, "").slice(0, 6))}
                      placeholder="Enter 6-digit pincode"
                      className={`w-full pl-12 pr-4 py-3 border rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0D9488] focus:border-transparent transition-all ${errors.pincode ? "border-[#0D9488]" : "border-[#D1D5DB]"
                        }`}
                    />
                  </div>
                  {errors.pincode && (
                    <p className="text-sm text-[#0D9488] mt-1 flex items-center gap-1">
                      <AlertCircle className="w-4 h-4" />
                      {errors.pincode}
                    </p>
                  )}
                  {isPincodeLoading && <p className="text-sm text-[#6B7280] mt-1">Fetching location details...</p>}
                  {!isPincodeLoading && city && state && (
                    <p className="text-sm text-[#0D9488] mt-1 flex items-center gap-1">
                      <Check className="w-4 h-4" />
                      {city}, {district}, {state}, {country}
                    </p>
                  )}
                </div>

                {/* Terms & Conditions */}
                <div>
                  <label className="flex items-start gap-3 cursor-pointer">
                    <div
                      className={`w-5 h-5 rounded border-2 flex items-center justify-center flex-shrink-0 mt-0.5 transition-all ${agreedToTerms
                        ? "bg-[#0D9488] border-[#0D9488]"
                        : errors.terms
                          ? "border-[#0D9488]"
                          : "border-[#D1D5DB]"
                        }`}
                    >
                      {agreedToTerms && <Check className="w-3 h-3 text-white" />}
                    </div>
                    <input
                      type="checkbox"
                      checked={agreedToTerms}
                      onChange={(e) => setAgreedToTerms(e.target.checked)}
                      className="sr-only"
                    />
                    <span className="text-sm text-[#6B7280]">
                      I agree to the{" "}
                      <a href="#" className="text-[#0D9488] hover:underline">
                        Terms & Conditions
                      </a>{" "}
                      and{" "}
                      <a href="#" className="text-[#0D9488] hover:underline">
                        Privacy Policy
                      </a>
                    </span>
                  </label>
                  {errors.terms && (
                    <p className="text-sm text-[#0D9488] mt-1 flex items-center gap-1">
                      <AlertCircle className="w-4 h-4" />
                      {errors.terms}
                    </p>
                  )}
                </div>

                {/* Submit Button */}
                <button
                  type="submit"
                  disabled={isLoading}
                  className="w-full py-3 bg-gradient-to-r from-[#14B8A6] to-[#0D9488] text-white rounded-xl font-semibold hover:opacity-90 transition-opacity flex items-center justify-center gap-2"
                >
                  {isLoading ? (
                    <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  ) : (
                    <>
                      Continue
                      <ArrowRight className="w-5 h-5" />
                    </>
                  )}
                </button>

                {/* Login Link */}
                <p className="text-center text-[#6B7280]">
                  Already have an account?{" "}
                  <Link href="/login" className="text-[#0D9488] font-medium hover:underline">
                    Sign In
                  </Link>
                </p>
              </motion.form>
            )}

            {step === "otp" && (
              <motion.div
                key="otp"
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
              >
                <div className="flex justify-center gap-2 mb-6">
                  {otp.map((digit, index) => (
                    <input
                      key={index}
                      ref={(el) => {
                        otpRefs.current[index] = el
                      }}
                      type="text"
                      inputMode="numeric"
                      value={digit}
                      onChange={(e) => handleOtpChange(index, e.target.value)}
                      onKeyDown={(e) => handleOtpKeyDown(index, e)}
                      className="w-12 h-14 text-center text-2xl font-semibold border border-[#D1D5DB] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#0D9488] focus:border-transparent"
                    />
                  ))}
                </div>

                <button
                  onClick={handleVerifyOtp}
                  disabled={otp.join("").length !== 6 || isLoading}
                  className={`w-full py-3 rounded-xl font-semibold flex items-center justify-center gap-2 transition-all ${otp.join("").length === 6 && !isLoading
                    ? "bg-gradient-to-r from-[#14B8A6] to-[#0D9488] text-white hover:opacity-90"
                    : "bg-[#E5E7EB] text-[#9CA3AF] cursor-not-allowed"
                    }`}
                >
                  {isLoading ? (
                    <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  ) : (
                    <>
                      Verify & Create Account
                      <Check className="w-5 h-5" />
                    </>
                  )}
                </button>

                <div className="mt-4 text-center">
                  <button
                    onClick={handleResendOtp}
                    disabled={timer > 0 || isLoading}
                    className={`text-sm ${timer > 0 ? "text-[#9CA3AF]" : "text-[#0D9488] hover:underline"}`}
                  >
                    {timer > 0 ? `Resend OTP in ${timer}s` : "Resend OTP"}
                  </button>
                </div>

                <button
                  onClick={() => setStep("details")}
                  className="w-full mt-4 py-2 text-[#6B7280] hover:text-[#1F2937] text-sm"
                >
                  ← Change mobile number
                </button>
              </motion.div>
            )}

            {step === "success" && (
              <motion.div
                key="success"
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                className="text-center py-8"
              >
                <div className="w-20 h-20 bg-[#10B981] rounded-full flex items-center justify-center mx-auto mb-6">
                  <Check className="w-10 h-10 text-white" />
                </div>
                <h2 className="text-2xl font-bold text-[#1F2937] mb-2">Account Created!</h2>
                <p className="text-[#6B7280] mb-6">Welcome to Online Tyre Bazaar, {name.split(" ")[0]}!</p>
                <div className="w-8 h-8 border-2 border-[#0D9488] border-t-transparent rounded-full animate-spin mx-auto" />
                <p className="text-sm text-[#9CA3AF] mt-4">Redirecting to homepage...</p>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </motion.div>
    </div>
  )
}
