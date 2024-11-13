// src/components/EditUser.js

import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { TextField, Button, Typography } from "@mui/material";
import { fetchUserProfile, editUser } from '../services/userService';

const EditUser = () => {
  const { userId } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [error, setError] = useState(null);

  useEffect(() => {
    const loadUser = async () => {
      try {
        const userData = await fetchUserProfile(userId);
        setUser(userData);
      } catch (err) {
        setError('Failed to load user data.');
      }
    };

    loadUser();
  }, [userId]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setUser(prevUser => ({ ...prevUser, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await editUser(user);
      navigate("/admin"); // Redirect to admin dashboard after saving changes
    } catch (err) {
      setError('Failed to save user data. Please try again.');
    }
  };

  if (!user) return <Typography>Loading...</Typography>;

  return (
    <div style={{ padding: "20px", maxWidth: "600px", margin: "auto", color: "white" }}>
      <Typography variant="h4" gutterBottom style={{ color: "white" }}>
        Edit User
      </Typography>
      {error && <Typography style={{ color: "red" }}>{error}</Typography>}
      <form onSubmit={handleSubmit}>
        <TextField
          label="Username"
          name="username"
          value={user.username || ''}
          onChange={handleChange}
          fullWidth
          margin="normal"
          InputProps={{
            style: { color: "white" }
          }}
        />
        <TextField
          label="Email"
          name="email"
          value={user.email || ''}
          onChange={handleChange}
          fullWidth
          margin="normal"
          InputProps={{
            style: { color: "white" }
          }}
        />
        {/* Additional fields can be added here if needed */}
        <Button
          type="submit"
          variant="contained"
          style={{
            marginTop: "20px",
            color: "white",
            backgroundColor: "transparent",
            borderColor: "white",
            transition: "background-color 0.5s ease, color 0.5s ease",
          }}
          sx={{
            "&:hover": {
              backgroundColor: "black",
            },
          }}
        >
          Save Changes
        </Button>
      </form>
    </div>
  );
};

export default EditUser;
