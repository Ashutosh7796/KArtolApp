import React, { useState } from "react";
import "./DetailedResult.css";
import resultImg from "../../../assets/Images/result.png";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";
 
function DetailedResult({ student, onBackClick }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const toggleSidebar = () => setSidebarOpen((s) => !s);
  const closeSidebar = () => setSidebarOpen(false);
 
  const resultData = {
    candidateName: student.candidateName || "N/A",
    examName: student.examName || "N/A",
    subjectName: student.subjectName || "N/A",
    className: student.className || "N/A",
    rollNumber: student.rollNumber || "N/A",
 
    
    totalQuestions: student.totalQuestions ?? 0,
    unattemptedQuestions: student.unattemptedQuestions ?? 0,
    correctAnswers: student.correctAnswers ?? 0,
    incorrectAnswers: student.incorrectAnswers ?? 0,
 
    totalAttempted:
      (student.totalQuestions ?? 0) - (student.unattemptedQuestions ?? 0),
 
    score: `${student.score ?? 0}/${student.totalMarks ?? 0}`,
  };
 
  
  const totalQuestions = student.totalQuestions ?? 0;
  const correctAnswers = student.correctAnswers ?? 0;
  const totalMarks = student.totalMarks ?? 100;
  const rawScore = student.score ?? 0;
 
  let percentage = 0;
 
  
  if (rawScore <= 100 && totalMarks === 100) {
    percentage = rawScore;
  }
  
  else if (correctAnswers && totalQuestions) {
    percentage = Math.round((correctAnswers / totalQuestions) * 100);
  }

  else if (totalMarks > 0) {
    percentage = Math.round((rawScore / totalMarks) * 100);
  }
 
  
  percentage = Math.min(Math.max(percentage, 2), 98);
 
  
  const getGirlPosition = () => `${percentage}%`;
 
  return (
    <>
      <Header />
 
      <div className="detailed-result-page">
        <Sidebar isOpen={sidebarOpen} onClose={closeSidebar} />
 
        <div className="result-toolbar">
          <div className="toolbar-left">
            <button
              className="hamburger"
              aria-label={sidebarOpen ? "Close menu" : "Open menu"}
              aria-expanded={sidebarOpen}
              onClick={toggleSidebar}
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="20"
                height="14"
                viewBox="0 0 20 14"
                fill="none"
                aria-hidden="true"
                focusable="false"
              >
                <rect width="20" height="2" rx="1" fill="currentColor" />
                <rect y="6" width="12" height="2" rx="1" fill="currentColor" />
                <rect y="12" width="20" height="2" rx="1" fill="currentColor" />
              </svg>
            </button>
            <div className="page-title">Result</div>
          </div>
 
          <div className="toolbar-controls">
            {onBackClick && (
              <button className="back-button" onClick={onBackClick}>
                ← Back
              </button>
            )}
          </div>
        </div>
 
        <div className="result-header-details">
          <div className="card-box">
            <div className="card-item">
              <span className="card-label">Candidate Name -</span>
              <span className="card-value">{resultData.candidateName}</span>
            </div>
            <div className="card-item">
              <span className="card-label">Exam Name -</span>
              <span className="card-value">{resultData.examName}</span>
            </div>
            <div className="card-item">
              <span className="card-label">Subject Name -</span>
              <span className="card-value">{resultData.subjectName}</span>
            </div>
          </div>
 
          <div className="card-box">
            <div className="card-item">
              <span className="card-label">Class -</span>
              <span className="card-value">{resultData.className}</span>
            </div>
            <div className="card-item">
              <span className="card-label">Roll Number -</span>
              <span className="card-value">{resultData.rollNumber}</span>
            </div>
          </div>
        </div>
 
        <div className="score-image-wrapper">
          <div className="score-axis">
            <img
              src={resultImg}
              alt="Girl with flag"
              className="score-image"
              style={{ left: getGirlPosition() }}
            />
 
            <div className="score-axis-line"></div>
 
            <div className="score-axis-labels">
              {Array.from({ length: 11 }, (_, i) => (
                <span key={i} className="score-axis-label">
                  {i * 10}
                </span>
              ))}
            </div>
          </div>
        </div>
 
        <div className="score-card">
          <h3>Score Card</h3>
          <div className="score-details">
            <div className="score-item">
              <span className="score-label">Total Questions:</span>
              <span className="score-value">{resultData.totalQuestions}</span>
            </div>
            <div className="score-item">
              <span className="score-label">Total Attempted:</span>
              <span className="score-value">{resultData.totalAttempted}</span>
            </div>
            <div className="score-item">
              <span className="score-label">Total Unattempted:</span>
              <span className="score-value">
                {resultData.unattemptedQuestions}
              </span>
            </div>
            <div className="score-item">
              <span className="score-label">Correct Answers:</span>
              <span className="score-value">{resultData.correctAnswers}</span>
            </div>
            <div className="score-item">
              <span className="score-label">Incorrect Answers:</span>
              <span className="score-value">{resultData.incorrectAnswers}</span>
            </div>
            <div className="score-item">
              <span className="score-label">Score:</span>
              <span className="score-value">{resultData.score}</span>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
 
export default DetailedResult;