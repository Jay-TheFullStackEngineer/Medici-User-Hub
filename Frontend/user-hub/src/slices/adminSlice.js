import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { fetchAllUsers, editUser, deleteUser } from '../services/userService';

const initialState = {
  users: [],
  status: 'idle',
  error: null,
};

// Async thunk to fetch all users
export const fetchUsers = createAsyncThunk('admin/fetchUsers', async () => {
  return await fetchAllUsers();
});

// Async thunk to edit a user
export const adminEditUser = createAsyncThunk('admin/editUser', async (userData) => {
  return await editUser(userData);
});

// Async thunk to delete a user
export const adminDeleteUser = createAsyncThunk('admin/deleteUser', async (userId) => {
  return await deleteUser(userId);
});

const adminSlice = createSlice({
  name: 'admin',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchUsers.fulfilled, (state, action) => {
        state.users = action.payload;
      })
      .addCase(adminEditUser.fulfilled, (state, action) => {
        const index = state.users.findIndex(user => user.id === action.payload.id);
        if (index !== -1) state.users[index] = action.payload;
      })
      .addCase(adminDeleteUser.fulfilled, (state, action) => {
        state.users = state.users.filter(user => user.id !== action.meta.arg);
      });
  },
});

export default adminSlice.reducer;