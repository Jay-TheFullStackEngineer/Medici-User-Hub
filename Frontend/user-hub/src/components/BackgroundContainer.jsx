// src/components/BackgroundContainer.jsx

import React from "react";
import "./BackgroundContainer.css";

const BackgroundContainer = () => {
  return (
    <div className="background-container">
      <img
        src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/1231630/moon2.png"
        alt="Moon"
        className="moon"
      />
      <div className="stars"></div>
      <div className="twinkling"></div>
      <div className="clouds"></div>
    </div>
  );
};

export default BackgroundContainer;
