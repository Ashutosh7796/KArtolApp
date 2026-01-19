
import React, { useEffect, useState } from "react";
import {
  FaThLarge,
  FaCalendarAlt,
  FaChalkboardTeacher,
  FaUserGraduate,
  FaMoneyCheckAlt,
  FaUser,
  FaSignOutAlt,
  FaBars,
  FaFileAlt,
} from "react-icons/fa";
import { FiChevronDown, FiChevronUp } from "react-icons/fi";
import { Link, useLocation, useNavigate } from "react-router-dom";
import "./SideBar.css";

const MOBILE_BREAKPOINT = 768;

const Sidebar = ({ isOpen = undefined, onClose = undefined, showFloating = false }) => {
  const [internalOpen, setInternalOpen] = useState(window.innerWidth > MOBILE_BREAKPOINT);
  const open = typeof isOpen === "boolean" ? isOpen : internalOpen;

  const [openTeachers, setOpenTeachers] = useState(true);
  const [openStudents, setOpenStudents] = useState(true);
  const [openPapers, setOpenPapers] = useState(true);
  const [openFees, setOpenFees] = useState(true);
  const location = useLocation();
  const navigate = useNavigate();

  
  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth <= MOBILE_BREAKPOINT) {
        if (typeof isOpen !== "boolean") setInternalOpen(false);
      } else {
        if (typeof isOpen !== "boolean") setInternalOpen(true);
      }
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  
  }, []);

  
  useEffect(() => {
    if (window.innerWidth <= MOBILE_BREAKPOINT) {
      if (typeof isOpen !== "boolean") setInternalOpen(false);
      onClose && onClose();
    }
   
  }, [location.pathname]);

  const toggleOpen = () => {
    if (typeof isOpen === "boolean") {
      onClose && onClose();
    } else {
      setInternalOpen((s) => !s);
    }
  };

  const handleClose = () => {
    if (typeof isOpen === "boolean") {
      onClose && onClose();
    } else {
      setInternalOpen(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    sessionStorage.clear();
    navigate("/login");
  };

  return (
    <>
      <div
        className={`sb-overlay ${open ? "sb-visible" : ""}`}
        onClick={handleClose}
        aria-hidden={!open}
      />

      <aside
        className={`sb-sidebar ${open ? "sb-open" : ""}`}
        aria-hidden={!open && window.innerWidth <= MOBILE_BREAKPOINT}
      >
    
        <div className="sb-header-mobile">
          <button
            className="sb-menu-icon sb-mobile-close"
            onClick={handleClose}
            aria-label="Close menu"
            title="Close menu"
          >
            <FaBars />
          </button>
        </div>

        <div className="sb-top">
          <ul>
            <li className={location.pathname === "/dashboard" ? "active" : ""}>
              <Link to="/dashboard">
                <FaThLarge className="sb-icon" />
                <span>Dashboard</span>
              </Link>
            </li>

            <li className={location.pathname === "/calender" ? "active" : ""}>
              <Link to="/calender">
                <FaCalendarAlt className="sb-icon" />
                <span>Calendar</span>
              </Link>
            </li>

            <li
              className="sb-dropdown"
              onClick={() => setOpenTeachers((s) => !s)}
              role="button"
              aria-expanded={openTeachers}
            >
              <FaChalkboardTeacher className="sb-icon" />
              <span style={{ flex: 1 }}>Teachers</span>
              {openTeachers ? <FiChevronUp /> : <FiChevronDown />}
            </li>
            {openTeachers && (
              <ul className="sb-submenu">
                <li className={location.pathname === "/teacher/list" ? "active" : ""}>
                  <Link to="/teacher/list">List</Link>
                </li>
                <li className={location.pathname === "/teacher/papers" ? "active" : ""}>
                  <Link to="/teacher/papers">Papers</Link>
                </li>
                <li className={location.pathname === "/teacher/question-bank" ? "active" : ""}>
                  <Link to="/teacher/question-bank">Question Bank</Link>
                </li>
                <li className={location.pathname === "/teacher/salary" ? "active" : ""}>
                  <Link to="/teacher/salary">Salary</Link>
                </li>
                 <li className={location.pathname === "/teacher/attendance-summary" ? "active" : ""}>
                  <Link to="/teacher/attendance-summary">Attendance Summary</Link>
                </li>
              </ul>
            )}

            <li
              className="sb-dropdown"
              onClick={() => setOpenStudents((s) => !s)}
              role="button"
              aria-expanded={openStudents}
            >
              <FaUserGraduate className="sb-icon" />
              <span style={{ flex: 1 }}>Students</span>
              {openStudents ? <FiChevronUp /> : <FiChevronDown />}
            </li>
            {openStudents && (
              <ul className="sb-submenu">
                <li className={location.pathname === "/students/attendence" ? "active" : ""}>
                  <Link to="/students/attendence">Attendance</Link>
                </li>
                <li className={location.pathname === "/students/exams" ? "active" : ""}>
                  <Link to="/students/exams">Exams</Link>
                </li>
                <li className={location.pathname === "/students/results" ? "active" : ""}>
                  <Link to="/students/results">Results</Link>
                </li>
              </ul>
            )}

    {/* Fees dropdown */}
            <li
              className="sb-dropdown"
              onClick={() => setOpenFees((s) => !s)}
              role="button"
              aria-expanded={openFees}
            >
              <FaMoneyCheckAlt className="sb-icon" />
              <span style={{ flex: 1 }}>Fees</span>
              {openFees ? <FiChevronUp /> : <FiChevronDown />}
            </li>
            {openFees && (
              <ul className="sb-submenu">
                <li className={location.pathname === "/fees" ? "active" : ""}>
                  <Link to="/fees">Fees</Link>
                </li>
                <li className={location.pathname === "/fees/edit" ? "active" : ""}>
                  <Link to="/fees/edit">Edit Fees</Link>
                </li>
              </ul>
            )}


            <li
              className="sb-dropdown"
              onClick={() => setOpenPapers((s) => !s)}
              role="button"
              aria-expanded={openPapers}
            >
              <FaFileAlt className="sb-icon" />
              <span style={{ flex: 1 }}>Papers</span>
              {openPapers ? <FiChevronUp /> : <FiChevronDown />}
            </li>
            {openPapers && (
              <ul className="sb-submenu">
                <li className={location.pathname === "/papers" ? "active" : ""}>
                  <Link to="/papers">Papers</Link>
                </li>
                <li className={location.pathname === "/papers/paper-patterns" ? "active" : ""}>
                  <Link to="/papers/paper-patterns">Create Paper Patterns</Link>
                </li>
  
              </ul>
            )}
          </ul>
        </div>

        <div className="sb-bottom">
          <ul>
            <li className={location.pathname === "/profile" ? "active" : ""}>
              <Link to="/profile">
                <FaUser className="sb-icon" />
                <span>Profile</span>
              </Link>
            </li>

            <li>
              <button onClick={handleLogout} className="sb-logout-button" aria-label="Logout">
                <FaSignOutAlt className="sb-icon" />
                <span>Logout</span>
              </button>
            </li>
          </ul>
        </div>
      </aside>

      {showFloating && (
        <button
          className="sb-menu-icon sb-mobile-floating"
          onClick={toggleOpen}
          aria-label={open ? "Close menu" : "Open menu"}
          title="Toggle menu"
        >
          <FaBars />
        </button>
      )}
    </>
  );
};

export default Sidebar;
