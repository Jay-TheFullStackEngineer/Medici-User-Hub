// src/components/AdminDashboard.js

import React, { useState, useEffect } from "react";
import { Typography, Button, Table, TableBody, TableCell, TableHead, TableRow, IconButton } from "@mui/material";
import { Edit, Delete } from "@mui/icons-material";
import { fetchAllUsers, deleteUser } from '../services/userService'; // Import deleteUser and fetchAllUsers
import { useNavigate } from 'react-router-dom';

const AdminDashboard = ({ onLogout }) => {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const loadUsers = async () => {
      try {
        const response = await fetchAllUsers();
        setUsers(response);
      } catch (err) {
        setError('Failed to load users. Please try again later.');
      }
    };

    loadUsers();
  }, []);

  const handleEditUser = (userId) => {
    navigate(`/edit-user/${userId}`); // Navigate to the edit page with the userId
  };

  const handleDeleteUser = async (userId) => {
    try {
      await deleteUser(userId);
      setUsers(users.filter(user => user.id !== userId)); // Remove user from state
    } catch (err) {
      setError('Failed to delete user. Please try again.');
    }
  };

  return (
    <div style={{ padding: "20px", maxWidth: "800px", margin: "auto", color: "white" }}>
      <Typography variant="h4" gutterBottom style={{ color: "white" }}>
        Admin Dashboard
      </Typography>

      {error && <Typography style={{ color: "red" }}>{error}</Typography>}

      <Table>
        <TableHead>
          <TableRow>
            <TableCell style={{ color: "white" }}>Username</TableCell>
            <TableCell style={{ color: "white" }}>Email</TableCell>
            <TableCell style={{ color: "white" }}>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.id}>
              <TableCell style={{ color: "white" }}>{user.username}</TableCell>
              <TableCell style={{ color: "white" }}>{user.email}</TableCell>
              <TableCell>
                <IconButton
                  onClick={() => handleEditUser(user.id)}
                  sx={{
                    color: "white",
                    transition: "background-color 0.3s ease",
                    "&:hover": {
                      backgroundColor: "black",
                    },
                  }}
                >
                  <Edit />
                </IconButton>
                <IconButton
                  onClick={() => handleDeleteUser(user.id)}
                  sx={{
                    color: "white",
                    transition: "background-color 0.3s ease",
                    "&:hover": {
                      backgroundColor: "black",
                    },
                  }}
                >
                  <Delete />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <Button
        onClick={onLogout}
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
        Logout
      </Button>
    </div>
  );
};

export default AdminDashboard;
