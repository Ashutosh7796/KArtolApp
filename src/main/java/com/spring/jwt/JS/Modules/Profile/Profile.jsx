import React, { useState, useRef, useEffect } from "react";
import Header from "../../Components/Header/Header";
import Sidebar from "../../Components/SideBar/SideBar";
import { FiEdit2 } from "react-icons/fi";
import profileImg from "../../assets/Images/Profile_image.png";
import "./Profile.css";
import ProfileApi from "../../service/ProfileApi/ProfileApi";

function Profile() {
  const USER_ID = 10000;

  const [isEditing, setIsEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState(profileImg);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [phoneError, setPhoneError] = useState("");
  const fileInputRef = useRef(null);

  const [formData, setFormData] = useState({
    fullName: "",
    username: "",
    phone: "",
  });

  
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await ProfileApi.getProfile(USER_ID);
        setFormData({
          fullName: res.data?.fullName || "",
          username: res.data?.userName || "",
          phone: res.data?.mobileNumber
            ? String(res.data.mobileNumber)
            : "",
        });
      } catch (err) {
        console.error("Profile fetch error:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const handleEditClick = () => setIsEditing(true);

  const handleCancelClick = () => {
    setIsEditing(false);
    setPhoneError("");
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    if (name === "phone") {
      const digitsOnly = value.replace(/\D/g, "");

      if (digitsOnly.length !== 10) {
        setPhoneError("Mobile number must be exactly 10 digits");
      } else {
        setPhoneError("");
      }

      setFormData((prev) => ({ ...prev, phone: digitsOnly }));
      return;
    }

    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSaveClick = async (e) => {
    e.preventDefault();

    if (phoneError || formData.phone.length !== 10) {
      alert("Please enter a valid 10-digit mobile number");
      return;
    }

    const payload = {
      fullName: formData.fullName,
      userName: formData.username,
      mobileNumber: formData.phone,
    };

    try {
      await ProfileApi.updateProfile(USER_ID, payload);
      setIsEditing(false);
      alert("Profile updated successfully!");
    } catch (err) {
      console.error("Profile update error:", err);
      alert("Failed to update profile");
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedImage(URL.createObjectURL(file));
    }
  };

  const handleChangePhotoClick = () => {
    if (fileInputRef.current) fileInputRef.current.click();
  };

  const toggleSidebar = () => setSidebarOpen((prev) => !prev);

  return (
    <>
      <Header />
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      <div className="Profile-root">
        <div className="Profile-topbar">
          <button
            className="Profile-hamburger"
            onClick={toggleSidebar}
            type="button"
          >
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="14">
              <rect width="20" height="2" rx="1" />
              <rect y="6" width="12" height="2" rx="1" />
              <rect y="12" width="20" height="2" rx="1" />
            </svg>
          </button>
          <h1 className="profile-title">Profile</h1>
        </div>

        <div className="Profile-outerCard">
          <div className="Profile-container">
            
            <div className="Profile-card">
              <div className="Profile-photoSection">
                <div className="Profile-photoHeader">Change Photo</div>
                <div className="Profile-photoContent">
                  <img
                    src={selectedImage}
                    alt="Profile"
                    className="Profile-avatar"
                  />
                  <button
                    type="button"
                    className="Profile-changePhotoBtn"
                    onClick={handleChangePhotoClick}
                  >
                    Change
                  </button>
                  <input
                    type="file"
                    ref={fileInputRef}
                    accept="image/*"
                    style={{ display: "none" }}
                    onChange={handleImageChange}
                  />
                </div>
              </div>
            </div>

        
            {loading ? (
              <div style={{ padding: 20 }}>Loading profile...</div>
            ) : (
              <div className="Profile-infoCard">
                <div className="Profile-infoHeader">
                  <span className="Profile-infoTitle">
                    Personal Information
                  </span>

                  {!isEditing && (
                    <button
                      className="Profile-editBtn"
                      onClick={handleEditClick}
                      type="button"
                    >
                      <FiEdit2 size={16} style={{ marginRight: 6 }} />
                      Edit
                    </button>
                  )}
                </div>

                <form className="Profile-form" onSubmit={handleSaveClick}>
                  <div className="Profile-formRow">
                  
                    <div className="Profile-formGroup">
                      <label className="Profile-label">Full Name</label>
                      {isEditing ? (
                        <input
                          name="fullName"
                          value={formData.fullName}
                          onChange={handleInputChange}
                          className="Profile-input"
                        />
                      ) : (
                        <span className="Profile-readOnly">
                          {formData.fullName || "-"}
                        </span>
                      )}
                    </div>

                    
                    <div className="Profile-formGroup">
                      <label className="Profile-label">Username</label>
                      {isEditing ? (
                        <input
                          name="username"
                          value={formData.username}
                          onChange={handleInputChange}
                          className="Profile-input"
                        />
                      ) : (
                        <span className="Profile-readOnly">
                          {formData.username || "-"}
                        </span>
                      )}
                    </div>

                    
                    <div className="Profile-formGroup">
                      <label className="Profile-label">Phone Number</label>
                      {isEditing ? (
                        <>
                          <input
                            name="phone"
                            value={formData.phone}
                            onChange={handleInputChange}
                            className="Profile-input"
                          />
                          {phoneError && (
                            <span className="Profile-errorText">
                              {phoneError}
                            </span>
                          )}
                        </>
                      ) : (
                        <span className="Profile-readOnly">
                          {formData.phone || "-"}
                        </span>
                      )}
                    </div>
                  </div>

                  {isEditing && (
                    <div className="Profile-btnRow">
                      <button type="submit" className="Profile-saveBtn">
                        Save
                      </button>
                      <button
                        type="button"
                        className="Profile-cancelBtn"
                        onClick={handleCancelClick}
                      >
                        Cancel
                      </button>
                    </div>
                  )}
                </form>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}

export default Profile;
