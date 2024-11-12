// src/App.js

import React, { useState } from "react";
import { BrowserRouter as Router, Routes, Route, Navigate, useNavigate } from "react-router-dom";
import { Button, Typography } from "@mui/material";
import RegistrationForm from "./components/RegistrationForm";
import LoginForm from "./components/LoginForm";
import AdminDashboard from "./components/AdminDashboard";
import UserDashboard from "./components/UserDashboard";
import BackgroundContainer from "./components/BackgroundContainer";
import './App.css';

function AuthContainer({ onAuth, showRegister, toggleForm }) {
  return (
    <div className="relative min-h-screen flex items-center justify-center overflow-hidden">
      <BackgroundContainer />
      <div className="glassmorphic-container">
        <Typography variant="h4" align="center" gutterBottom style={{ color: '#fff' }}>
          {showRegister ? "Register" : "Login"}
        </Typography>

        {showRegister ? (
          <RegistrationForm onAuth={onAuth} />
        ) : (
          <LoginForm onAuth={onAuth} />
        )}

        <Button
          onClick={toggleForm}
          variant="text"
          fullWidth
          sx={{
            marginTop: 2,
            color: '#fff',
            backgroundColor: 'transparent',
            transition: 'background-color 0.5s ease, color 0.3s ease',
            '&:hover': {
              backgroundColor: '#000',
              color: '#fff',
            },
          }}
        >
          {showRegister ? "Already have an account? Login" : "New user? Register"}
        </Button>
      </div>
    </div>
  );
}

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
              <AdminDashboard handleLogout={handleLogout} />
            ) : (
              <Navigate to="/" />
            )
          }
        />
        <Route
          path="/user"
          element={
            isAuthenticated && userRole === 'user' ? (
              <UserDashboard handleLogout={handleLogout} />
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
