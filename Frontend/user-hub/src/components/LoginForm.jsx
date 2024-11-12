// src/components/LoginForm.jsx

import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { TextField } from '@mui/material';
import { login } from '../slices/authSlice'; // Import the login thunk
import './Shiny.css';

const LoginForm = ({ onAuth }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { isAuthenticated, error } = useSelector((state) => state.auth);
  const { profile } = useSelector((state) => state.user); // Assuming user profile is stored in user slice

  const formik = useFormik({
    initialValues: {
      username: '',
      password: '',
    },
    validationSchema: Yup.object({
      username: Yup.string().required('Username is required'),
      password: Yup.string().min(6, 'Password should be at least 6 characters').required('Password is required'),
    }),
    onSubmit: (values) => {
      dispatch(login(values)); // Dispatch login with form values
    },
  });

  useEffect(() => {
    if (isAuthenticated && profile) {
      const userRole = profile.role;
      onAuth(userRole); // Call onAuth to handle redirection based on role
      navigate(userRole === 'admin' ? '/admin' : '/user');
    }
  }, [isAuthenticated, profile, onAuth, navigate]);

  return (
    <div style={{ padding: '20px', width: '100%', maxWidth: '400px', margin: 'auto' }}>
      <form onSubmit={formik.handleSubmit}>
        {['username', 'password'].map((field) => (
          <TextField
            key={field}
            label={field.charAt(0).toUpperCase() + field.slice(1)}
            name={field}
            variant="filled"
            type={field === 'password' ? 'password' : 'text'}
            value={formik.values[field]}
            onChange={formik.handleChange}
            onBlur={formik.handleBlur}
            error={formik.touched[field] && Boolean(formik.errors[field])}
            helperText={formik.touched[field] && formik.errors[field]}
            fullWidth
            margin="normal"
            InputProps={{
              style: { fontSize: '1rem', height: '4rem', padding: '4px 12px' },
              disableUnderline: true,
            }}
            InputLabelProps={{ style: { fontSize: '1rem' } }}
            sx={{
              '& .MuiFilledInput-root': {
                backgroundColor: 'rgba(255, 255, 255, 0.1)',
                borderRadius: '8px',
                border: '1px solid rgba(255, 255, 255, 0.5)',
                padding: '4px 12px',
                '&:hover': { borderColor: 'rgba(255, 255, 255, 0.8)' },
                '&.Mui-focused': { borderColor: 'white' },
              },
            }}
          />
        ))}

        {/* Submit Button */}
        <button type="submit" className="shiny-cta">
          <span>Access My Account</span>
        </button>

        {/* Display error if login fails */}
        {error && (
          <div style={{ color: 'red', marginTop: '10px', textAlign: 'center' }}>
            {error || "Failed to log in. Please check your credentials and try again."}
          </div>
        )}
      </form>
    </div>
  );
};

export default LoginForm;
