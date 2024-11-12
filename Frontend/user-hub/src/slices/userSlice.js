import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { fetchUserProfile, updateUserProfile, deleteUser } from '../services/userService';

const initialState = {
  profile: null,
  status: 'idle',
  error: null,
};

// Async thunk to fetch user profile
export const fetchProfile = createAsyncThunk('user/fetchProfile', async () => {
  return await fetchUserProfile();
});

// Async thunk to update user profile
export const editProfile = createAsyncThunk('user/editProfile', async (updatedData) => {
  return await updateUserProfile(updatedData);
});

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchProfile.fulfilled, (state, action) => {
        state.profile = action.payload;
      })
      .addCase(editProfile.fulfilled, (state, action) => {
        state.profile = action.payload;
      });
  },
});

export default userSlice.reducer;