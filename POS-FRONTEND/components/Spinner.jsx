"use client"
import React from 'react';
import PropTypes from 'prop-types';

export default function Spinner({ color = 'bg-blue-500' }) {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-transparent pointer-events-none z-50">
      <div className="flex gap-2 items-center justify-center p-5">
        <span className="sr-only">Loading...</span>
        
        <div 
          className={`w-3 h-3 ${color} rounded-full animate-bounce [animation-delay:-0.3s]`} 
        />
        
        <div 
          className={`w-3 h-3 ${color} rounded-full animate-bounce [animation-delay:-0.15s]`} 
        />
        
        <div 
          className={`w-3 h-3 ${color} rounded-full animate-bounce`} 
        />
      </div>
    </div>
  );
}

Spinner.propTypes = {
  color: PropTypes.string,
};