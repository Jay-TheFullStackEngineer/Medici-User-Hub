// src/components/UserDashboard.js

import React, { useState } from "react";
import { Typography, Button, TextField, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

const UserDashboard = ({ onLogout }) => {
  const [isEditDialogOpen, setEditDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [userDetails, setUserDetails] = useState({
    username: "john_doe",
    email: "john@example.com",
    // Add other details as needed
  });

  const handleEditDetails = () => {
    console.log("Save user details", userDetails);
    setEditDialogOpen(false);
  };

  const handleDeleteAccount = () => {
    console.log("Delete user account");
    setDeleteDialogOpen(false);
  };

  return (
    <div style={{ padding: "20px", maxWidth: "600px", margin: "auto" }}>
      <Typography variant="h4" gutterBottom>
        User Dashboard
      </Typography>

      <div>
        <Typography variant="h6">Welcome, {userDetails.username}</Typography>
        <Typography>Email: {userDetails.email}</Typography>
      </div>

      <Button variant="outlined" onClick={() => setEditDialogOpen(true)} style={{ marginTop: "20px" }}>
        Update Details
      </Button>

      <Button variant="outlined" color="error" onClick={() => setDeleteDialogOpen(true)} style={{ marginTop: "20px", marginLeft: "10px" }}>
        Delete Account
      </Button>

      <Button onClick={onLogout} variant="contained" color="primary" style={{ marginTop: "20px", display: "block" }}>
        Logout
      </Button>

      {/* Edit Dialog */}
      <Dialog open={isEditDialogOpen} onClose={() => setEditDialogOpen(false)}>
        <DialogTitle>Edit Details</DialogTitle>
        <DialogContent>
          <DialogContentText>Update your account information below:</DialogContentText>
          <TextField
            margin="dense"
            label="Username"
            type="text"
            fullWidth
            value={userDetails.username}
            onChange={(e) => setUserDetails({ ...userDetails, username: e.target.value })}
          />
          <TextField
            margin="dense"
            label="Email"
            type="email"
            fullWidth
            value={userDetails.email}
            onChange={(e) => setUserDetails({ ...userDetails, email: e.target.value })}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleEditDetails} color="primary">Save</Button>
        </DialogActions>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <Dialog open={isDeleteDialogOpen} onClose={() => setDeleteDialogOpen(false)}>
        <DialogTitle>Delete Account</DialogTitle>
        <DialogContent>
          <DialogContentText>Are you sure you want to delete your account? This action is irreversible.</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setDeleteDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleDeleteAccount} color="error">Delete</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
};

export default UserDashboard;
