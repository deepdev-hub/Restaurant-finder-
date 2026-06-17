import React from 'react';
import { CheckCircle, AlertCircle, Info, X } from 'lucide-react';

export type AlertType = 'success' | 'error' | 'info';

interface CustomAlertProps {
  isOpen: boolean;
  type?: AlertType;
  title: string;
  message: string;
  confirmText?: string;
  onClose: () => void;
  onConfirm?: () => void;
}

export function CustomAlert({
  isOpen,
  type = 'info',
  title,
  message,
  confirmText = 'OK',
  onClose,
  onConfirm
}: CustomAlertProps) {
  if (!isOpen) return null;

  const handleConfirm = () => {
    if (onConfirm) onConfirm();
    onClose();
  };

  const getIcon = () => {
    switch (type) {
      case 'success':
        return <CheckCircle className="w-12 h-12 text-green-500 mb-4 mx-auto" />;
      case 'error':
        return <AlertCircle className="w-12 h-12 text-red-500 mb-4 mx-auto" />;
      case 'info':
      default:
        return <Info className="w-12 h-12 text-blue-500 mb-4 mx-auto" />;
    }
  };

  const getButtonClass = () => {
    switch (type) {
      case 'error':
        return 'bg-red-600 hover:bg-red-700 focus:ring-red-500';
      case 'success':
      case 'info':
      default:
        return 'hover:opacity-90 focus:ring-blue-500';
    }
  };

  const getButtonStyle = () => {
    if (type === 'success' || type === 'info') {
      return { background: "linear-gradient(135deg, #0066CC 0%, #004499 100%)" };
    }
    return {};
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm transition-opacity">
      <div 
        className="bg-white rounded-2xl shadow-xl w-full max-w-sm overflow-hidden transform transition-all animate-in fade-in zoom-in duration-200"
        role="dialog"
        aria-modal="true"
      >
        <div className="p-6 text-center relative">
          <button 
            onClick={onClose}
            className="absolute top-4 right-4 text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
          
          {getIcon()}
          
          <h3 className="text-xl font-bold text-gray-900 mb-2">
            {title}
          </h3>
          <p className="text-gray-600 mb-6">
            {message}
          </p>
          
          <button
            onClick={handleConfirm}
            className={`w-full py-3 px-4 text-white font-medium rounded-xl transition-all focus:outline-none focus:ring-2 focus:ring-offset-2 ${getButtonClass()}`}
            style={getButtonStyle()}
          >
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
