// src/components/AdminDashboard.js

import React, { useState, useEffect } from "react";
import { Typography, Button, Table, TableBody, TableCell, TableHead, TableRow, IconButton } from "@mui/material";
import { Edit, Delete } from "@mui/icons-material";

const AdminDashboard = ({ onLogout }) => {
  const [users, setUsers] = useState([]);

  // Simulated fetch function for users
  useEffect(() => {
    const fetchUsers = async () => {
      // Simulated API call to fetch users
      const response = [
        { id: 1, username: "john_doe", email: "john@example.com" },
        { id: 2, username: "jane_doe", email: "jane@example.com" },
      ];
      setUsers(response);
    };

    fetchUsers();
  }, []);

  const handleEditUser = (userId) => {
    console.log("Edit user", userId);
  };

  const handleDeleteUser = (userId) => {
    console.log("Delete user", userId);
  };

  return (
    <div style={{ padding: "20px", maxWidth: "800px", margin: "auto" }}>
      <Typography variant="h4" gutterBottom>
        Admin Dashboard
      </Typography>

      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Username</TableCell>
            <TableCell>Email</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.id}>
              <TableCell>{user.username}</TableCell>
              <TableCell>{user.email}</TableCell>
              <TableCell>
                <IconButton onClick={() => handleEditUser(user.id)}><Edit /></IconButton>
                <IconButton onClick={() => handleDeleteUser(user.id)}><Delete /></IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <Button onClick={onLogout} variant="contained" color="primary" style={{ marginTop: "20px" }}>
        Logout
      </Button>
    </div>
  );
};

export default AdminDashboard;
