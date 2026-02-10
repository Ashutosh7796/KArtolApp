
import React from "react";
import "./Header.css";


import SchoolLogo from "../../assets/Images/concept_school_logo.png";
import ProfileImage from "../../assets/Images/Profile_image.png";

const Header = ({ showProfile = true }) => {
  return (
    <header className="top-navbar">
      <div className="logo">
        <img src={SchoolLogo} alt="Concept School" className="school-logo" />
      </div>

      {/* Profile Image only if showProfile is true */}
      {showProfile && (
        <img src={ProfileImage} alt="Profile" className="profile-avatar" />
      )}
    </header>
  );
};

export default Header;
