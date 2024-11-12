// src/services/authService.js

// import axios from 'axios';  // Comment out axios
import mockUsers from '../app/mockUsers.json'; // Import mock data directly

// const API_BASE_URL = 'http://localhost:8080/api/users';  // Comment out the API base URL

// Register a new user
export const registerUser = async (userData) => {
  // Check if user already exists
  const userExists = mockUsers.some(user => user.username === userData.username);
  if (userExists) {
    throw new Error('User already exists');
  }

  // Add the new user to mockUsers
  const newUser = { ...userData, id: mockUsers.length + 1, role: 'user' }; // Assuming default role is 'user'
  mockUsers.push(newUser); // This does not persist; it modifies the in-memory data only

  return newUser; // Return the created user data (in real implementation, save changes)
};

// Login user
export const loginUser = async (credentials) => {
  // Find the user in mock data
  const user = mockUsers.find(
    (user) => user.username === credentials.username && user.password === credentials.password
  );
  
  if (user) {
    return 'mocked-auth-token';  // Return a mock token (replace with actual token handling if needed)
  } else {
    throw new Error('Invalid credentials');
  }
};

// Fetch user profile
export const fetchUserProfile = async (userId) => {
  const user = mockUsers.find(user => user.id === userId);
  if (!user) throw new Error('User not found');
  return user;
};

// Update user profile
export const updateUserProfile = async (userData) => {
  const index = mockUsers.findIndex(user => user.id === userData.id);
  if (index === -1) throw new Error('User not found');

  mockUsers[index] = { ...mockUsers[index], ...userData }; // Update the user data in memory
  return mockUsers[index];
};

// Fetch all users
export const fetchAllUsers = async () => {
  return mockUsers;
};

// Edit user (alias for updateUserProfile for simplicity)
export const editUser = async (userData) => {
  return await updateUserProfile(userData);
};

// Delete user
export const deleteUser = async (userId) => {
  const index = mockUsers.findIndex(user => user.id === userId);
  if (index === -1) throw new Error('User not found');

  mockUsers.splice(index, 1); // Remove user from mock data in memory
};
