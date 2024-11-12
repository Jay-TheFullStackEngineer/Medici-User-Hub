// src/components/RegistrationForm.jsx

import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { useFormik } from 'formik';
import * as Yup from 'yup';
import { TextField, Select, MenuItem, FormControl, InputLabel, Checkbox, FormControlLabel, Grid } from '@mui/material';
import { register } from '../slices/authSlice';
import './Shiny.css';

const RegistrationForm = ({ onAuth }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { isAuthenticated, error } = useSelector((state) => state.auth);

  const formik = useFormik({
    initialValues: {
      firstName: '',
      lastName: '',
      username: '',
      email: '',
      password: '',
      phone: '',
      countryCode: '+1', // Default to US
      dob: '',
      address: '',
      country: '',
      termsAgreed: false,
      preferredLanguage: 'English',
      securityQuestion: '',
      securityAnswer: '',
    },
    validationSchema: Yup.object({
      firstName: Yup.string().required('First Name is required'),
      lastName: Yup.string().required('Last Name is required'),
      username: Yup.string().required('Username is required'),
      email: Yup.string().email('Invalid email').required('Email is required'),
      password: Yup.string().min(6, 'Password should be at least 6 characters').required('Password is required'),
      phone: Yup.string().required('Phone number is required'),
      dob: Yup.date().required('Date of Birth is required'),
      address: Yup.string().required('Address is required'),
      country: Yup.string().required('Country is required'),
      termsAgreed: Yup.boolean().oneOf([true], 'You must agree to the terms and conditions'),
      preferredLanguage: Yup.string().required('Preferred Language is required'),
      securityQuestion: Yup.string().required('Security question is required'),
      securityAnswer: Yup.string().required('Security answer is required'),
    }),
    onSubmit: (values) => {
      dispatch(register(values));
    },
  });

  useEffect(() => {
    if (isAuthenticated) {
      onAuth && onAuth(); // Call onAuth if it exists
      navigate('/user'); // Redirect to the user dashboard after successful registration
    }
  }, [isAuthenticated, navigate, onAuth]);

  return (
    <div style={{ padding: '20px', width: '100%', maxWidth: '500px', margin: 'auto' }}>
      <form onSubmit={formik.handleSubmit}>
        {/* First Name */}
        <TextField
          label="First Name"
          name="firstName"
          variant="filled"
          value={formik.values.firstName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.firstName && Boolean(formik.errors.firstName)}
          helperText={formik.touched.firstName && formik.errors.firstName}
          fullWidth
          margin="normal"
        />

        {/* Last Name */}
        <TextField
          label="Last Name"
          name="lastName"
          variant="filled"
          value={formik.values.lastName}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.lastName && Boolean(formik.errors.lastName)}
          helperText={formik.touched.lastName && formik.errors.lastName}
          fullWidth
          margin="normal"
        />

        {/* Username */}
        <TextField
          label="Username"
          name="username"
          variant="filled"
          value={formik.values.username}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.username && Boolean(formik.errors.username)}
          helperText={formik.touched.username && formik.errors.username}
          fullWidth
          margin="normal"
        />

        {/* Email */}
        <TextField
          label="Email"
          name="email"
          variant="filled"
          type="email"
          value={formik.values.email}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.email && Boolean(formik.errors.email)}
          helperText={formik.touched.email && formik.errors.email}
          fullWidth
          margin="normal"
        />

        {/* Password */}
        <TextField
          label="Password"
          name="password"
          variant="filled"
          type="password"
          value={formik.values.password}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.password && Boolean(formik.errors.password)}
          helperText={formik.touched.password && formik.errors.password}
          fullWidth
          margin="normal"
        />

        {/* Phone Number with Country Code */}
        <Grid container spacing={1} alignItems="flex-end">
          <Grid item xs={4}>
            <FormControl variant="filled" fullWidth>
              <InputLabel>Code</InputLabel>
              <Select
                name="countryCode"
                value={formik.values.countryCode}
                onChange={formik.handleChange}
              >
                <MenuItem value="+1">+1 (US)</MenuItem>
                <MenuItem value="+44">+44 (UK)</MenuItem>
                <MenuItem value="+91">+91 (India)</MenuItem>
                {/* Add more country codes here */}
              </Select>
            </FormControl>
          </Grid>
          <Grid item xs={8}>
            <TextField
              label="Phone Number"
              name="phone"
              variant="filled"
              value={formik.values.phone}
              onChange={formik.handleChange}
              onBlur={formik.handleBlur}
              error={formik.touched.phone && Boolean(formik.errors.phone)}
              helperText={formik.touched.phone && formik.errors.phone}
              fullWidth
            />
          </Grid>
        </Grid>

        {/* Date of Birth */}
        <TextField
          label="Date of Birth"
          name="dob"
          variant="filled"
          type="date"
          value={formik.values.dob}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.dob && Boolean(formik.errors.dob)}
          helperText={formik.touched.dob && formik.errors.dob}
          fullWidth
          margin="normal"
          InputLabelProps={{ shrink: true }}
        />

        {/* Address */}
        <TextField
          label="Address"
          name="address"
          variant="filled"
          value={formik.values.address}
          onChange={formik.handleChange}
          onBlur={formik.handleBlur}
          error={formik.touched.address && Boolean(formik.errors.address)}
          helperText={formik.touched.address && formik.errors.address}
          fullWidth
          margin="normal"
        />

        {/* Country */}
        <FormControl variant="filled" fullWidth margin="normal">
          <InputLabel>Country</InputLabel>
          <Select
            name="country"
            value={formik.values.country}
            onChange={formik.handleChange}
          >
            <MenuItem value="United States">United States</MenuItem>
            <MenuItem value="United Kingdom">United Kingdom</MenuItem>
            <MenuItem value="Canada">Canada</MenuItem>
            {/* Add more countries as needed */}
          </Select>
        </FormControl>

        {/* Terms Agreement */}
        <FormControlLabel
          control={
            <Checkbox
              name="termsAgreed"
              color="primary"
              checked={formik.values.termsAgreed}
              onChange={formik.handleChange}
            />
          }
          label={
            <span>
              I agree to the{' '}
              <a href="https://example.com/terms" target="_blank" rel="noopener noreferrer">
                terms and conditions
              </a>
            </span>
          }
        />

        {/* Submit Button */}
        <button type="submit" className="shiny-cta">
          <span>Register Your Account</span>
        </button>

        {/* Display error if registration fails */}
        {error && (
          <div style={{ color: 'red', marginTop: '10px', textAlign: 'center' }}>
            {error || "Failed to register. Please try again."}
          </div>
        )}
      </form>
    </div>
  );
};

export default RegistrationForm;
