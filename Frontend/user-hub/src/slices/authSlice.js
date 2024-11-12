import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { loginUser, registerUser } from '../services/userService';

const initialState = {
  token: localStorage.getItem('authToken') || null,
  isAuthenticated: !!localStorage.getItem('authToken'),
  status: 'idle',
  error: null,
};

// Thunks for async actions
export const login = createAsyncThunk('auth/login', async (credentials) => {
  const token = await loginUser(credentials);
  return token;
});

export const register = createAsyncThunk('auth/register', async (userData) => {
  const token = await registerUser(userData);
  return token;
});

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    logout(state) {
      localStorage.removeItem('authToken');
      state.token = null;
      state.isAuthenticated = false;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(login.fulfilled, (state, action) => {
        state.token = action.payload;
        state.isAuthenticated = true;
        localStorage.setItem('authToken', action.payload);
      })
      .addCase(register.fulfilled, (state, action) => {
        state.token = action.payload;
        state.isAuthenticated = true;
        localStorage.setItem('authToken', action.payload);
      });
  },
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;