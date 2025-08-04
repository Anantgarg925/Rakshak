import { useState, useEffect } from "react";
import { SplashScreen } from "./components/SplashScreen";
import { HomeScreen } from "./components/HomeScreen";
import { AccidentDetectedScreen } from "./components/AccidentDetectedScreen";
import { HistoryScreen } from "./components/HistoryScreen";
import { SettingsScreen } from "./components/SettingsScreen";
import { ProfileScreen } from "./components/ProfileScreen";
import { AdminScreen } from "./components/AdminScreen";
import { Toaster } from "./components/ui/sonner";
import { toast } from "sonner@2.0.3";
import { Home, History, Settings, User } from "lucide-react";

type Screen = 
  | "splash" 
  | "home" 
  | "accident-detected" 
  | "history" 
  | "settings" 
  | "profile" 
  | "admin";

export default function App() {
  const [currentScreen, setCurrentScreen] = useState<Screen>("splash");
  const [isDetectionActive, setIsDetectionActive] = useState(false);

  // Automatically start detection after splash screen
  useEffect(() => {
    if (currentScreen === "home" && !isDetectionActive) {
      setTimeout(() => {
        setIsDetectionActive(true);
        toast.success("🔄 Automatic monitoring started", {
          description: "Real-time sensors are now active and monitoring"
        });
      }, 1000);
    }
  }, [currentScreen, isDetectionActive]);

  const handleSplashFinish = () => {
    setCurrentScreen("home");
  };

  const handleToggleDetection = () => {
    setIsDetectionActive(!isDetectionActive);
    if (!isDetectionActive) {
      toast.success("🟢 Accident detection activated", {
        description: "Real-time monitoring is now active"
      });
    } else {
      toast.info("🔴 Accident detection stopped", {
        description: "Emergency monitoring has been disabled"
      });
    }
  };

  const handleEmergencyAlert = () => {
    setCurrentScreen("accident-detected");
    toast.error("🚨 Emergency SOS Activated!", {
      description: "Immediate emergency response initiated"
    });
  };

  const handleAccidentDetected = () => {
    setCurrentScreen("accident-detected");
    toast.error("🚨 Accident Detected!", {
      description: "Emergency alert protocol activated"
    });
  };

  const handleCancelAlert = () => {
    setCurrentScreen("home");
    toast.success("✅ Emergency alert cancelled", {
      description: "False alarm reported successfully"
    });
  };

  const handleSendAlert = () => {
    setCurrentScreen("home");
    toast.success("🚑 Emergency alert sent successfully!", {
      description: "Help is on the way. Emergency services have been notified."
    });
  };

  const handleShowHistory = () => {
    setCurrentScreen("history");
  };

  const handleShowSettings = () => {
    setCurrentScreen("settings");
  };

  const handleShowProfile = () => {
    setCurrentScreen("profile");
  };

  const handleShowAdmin = () => {
    setCurrentScreen("admin");
  };

  const handleBackToHome = () => {
    setCurrentScreen("home");
  };

  const renderScreen = () => {
    switch (currentScreen) {
      case "splash":
        return <SplashScreen onFinish={handleSplashFinish} />;
      
      case "home":
        return (
          <HomeScreen
            isDetectionActive={isDetectionActive}
            onToggleDetection={handleToggleDetection}
            onEmergencyAlert={handleEmergencyAlert}
            onShowHistory={handleShowHistory}
            onShowSettings={handleShowSettings}
            onAccidentDetected={handleAccidentDetected}
          />
        );
      
      case "accident-detected":
        return (
          <AccidentDetectedScreen
            onCancel={handleCancelAlert}
            onSendAlert={handleSendAlert}
          />
        );
      
      case "history":
        return <HistoryScreen onBack={handleBackToHome} />;
      
      case "settings":
        return <SettingsScreen onBack={handleBackToHome} />;
      
      case "profile":
        return (
          <ProfileScreen 
            onBack={handleBackToHome} 
            onShowAdmin={handleShowAdmin}
          />
        );
      
      case "admin":
        return <AdminScreen onBack={() => setCurrentScreen("profile")} />;
      
      default:
        return <HomeScreen
          isDetectionActive={isDetectionActive}
          onToggleDetection={handleToggleDetection}
          onEmergencyAlert={handleEmergencyAlert}
          onShowHistory={handleShowHistory}
          onShowSettings={handleShowSettings}
          onAccidentDetected={handleAccidentDetected}
        />;
    }
  };

  const navigationItems = [
    { id: 'home', label: 'Home', icon: Home },
    { id: 'history', label: 'History', icon: History },
    { id: 'settings', label: 'Settings', icon: Settings },
    { id: 'profile', label: 'Profile', icon: User },
  ];

  return (
    <div className="min-h-screen bg-background">
      {renderScreen()}
      
      {/* Enhanced Bottom Navigation */}
      {currentScreen !== "splash" && currentScreen !== "accident-detected" && (
        <div className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 shadow-lg z-50">
          <div className="max-w-md mx-auto px-4 py-2">
            <div className="flex justify-around items-center">
              {navigationItems.map(({ id, label, icon: Icon }) => (
                <button
                  key={id}
                  onClick={() => setCurrentScreen(id as Screen)}
                  className={`flex flex-col items-center p-3 rounded-xl transition-all duration-200 ${
                    (currentScreen === id || (currentScreen === "admin" && id === "profile"))
                      ? "bg-red-100 text-red-600 shadow-sm" 
                      : "text-gray-600 hover:text-gray-900 hover:bg-gray-50"
                  }`}
                >
                  <Icon className="w-6 h-6 mb-1" />
                  <span className="text-xs font-medium">{label}</span>
                </button>
              ))}
            </div>
          </div>
        </div>
      )}
      
      <Toaster 
        position="top-center" 
        toastOptions={{
          duration: 4000,
          style: {
            borderRadius: '12px',
            padding: '16px',
            fontSize: '14px',
            fontWeight: '500',
          },
        }}
      />
    </div>
  );
}