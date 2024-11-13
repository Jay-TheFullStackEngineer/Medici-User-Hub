// src/App.js

import React, { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import AdminDashboard from "./components/AdminDashboard";
import UserDashboard from "./components/UserDashboard";
import EditUser from "./components/EditUser";
import AuthContainer from "./components/AuthContainer";
import BackgroundContainer from "./components/BackgroundContainer";
import './App.css';

function AppWithRouter() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState(null); // 'admin' or 'user'
  const [showRegister, setShowRegister] = useState(true);

  const toggleForm = () => setShowRegister((prev) => !prev);

  const handleAuthentication = (role) => {
    setIsAuthenticated(true);
    setUserRole(role);
  };

  const handleLogout = () => {
    setIsAuthenticated(false);
    setUserRole(null);
  };

  useEffect(() => {
    if (isAuthenticated) {
      if (userRole === 'admin') {
        window.location.href = "/admin";
      } else {
        window.location.href = "/user";
      }
    }
  }, [isAuthenticated, userRole]);

  return (
    <Router>
      <Routes>
        <Route
          path="/"
          element={
            !isAuthenticated ? (
              <AuthContainer
                onAuth={handleAuthentication}
                showRegister={showRegister}
                toggleForm={toggleForm}
              />
            ) : (
              <Navigate to={userRole === 'admin' ? "/admin" : "/user"} />
            )
          }
        />
        <Route
          path="/admin"
          element={
            isAuthenticated && userRole === 'admin' ? (
              <>
                <BackgroundContainer />
                <div className="glassmorphic-container">
                  <AdminDashboard onLogout={handleLogout} />
                </div>
              </>
            ) : (
              <Navigate to="/" />
            )
          }
        />
        <Route
          path="/user"
          element={
            isAuthenticated && userRole === 'user' ? (
              <>
                <BackgroundContainer />
                <div className="glassmorphic-container">
                  <UserDashboard onLogout={handleLogout} />
                </div>
              </>
            ) : (
              <Navigate to="/" />
            )
          }
        />
        <Route
          path="/edit-user/:userId"
          element={
            isAuthenticated && userRole === 'admin' ? (
              <>
                <BackgroundContainer />
                <div className="glassmorphic-container">
                  <EditUser />
                </div>
              </>
            ) : (
              <Navigate to="/" />
            )
          }
        />
      </Routes>
    </Router>
  );
}

export default AppWithRouter;
