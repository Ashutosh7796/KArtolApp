import React, { useEffect, useState } from "react";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";
import "./QuestionBank.css";

import {
  getAllStudentClass,
  getTeachersByClass,
  getSubjectsByClassAndTeacher,
  getQuestionPaper,
  getAllQuestionBank,
} from "../../../service/TeacherApi/QuestionBankApi";


import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Swal from "sweetalert2";

export default function QuestionBank() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const [classes, setClasses] = useState([]);
  const [studentClass, setStudentClass] = useState("");

  const [teachers, setTeachers] = useState([]);
  const [selectedTeacherId, setSelectedTeacherId] = useState("");

  const [subjects, setSubjects] = useState([]);
  const [selectedSubject, setSelectedSubject] = useState("");

  const [questions, setQuestions] = useState([]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    getAllQuestionBank()
      .then((data) => {
        setQuestions(data);
        toast.success("Question bank loaded successfully");
      })
      .catch((err) => {
        setError(err.message);
        Swal.fire("Error", "Failed to load question bank", "error");
      })
      .finally(() => setLoading(false));
  }, []);

 
  useEffect(() => {
    getAllStudentClass()
      .then((data) => {
        setClasses(data);
      })
      .catch(() => {
        Swal.fire("Error", "Failed to load classes", "error");
      });
  }, []);

  
  useEffect(() => {
    if (!studentClass) return;

    setLoading(true);
    setTeachers([]);
    setSubjects([]);
    setQuestions([]);
    setSelectedTeacherId("");
    setSelectedSubject("");

    getTeachersByClass(studentClass)
      .then((data) => {
        setTeachers(data);
        toast.success("Teachers loaded");
      })
      .catch((err) => {
        setError(err.message);
        Swal.fire("Error", "Failed to load teachers", "error");
      })
      .finally(() => setLoading(false));
  }, [studentClass]);

  
  useEffect(() => {
    if (!studentClass || !selectedTeacherId) return;

    setLoading(true);
    setSubjects([]);
    setQuestions([]);
    setSelectedSubject("");

    getSubjectsByClassAndTeacher(studentClass, selectedTeacherId)
      .then((data) => {
        setSubjects(data);
        toast.success("Subjects loaded");
      })
      .catch((err) => {
        setError(err.message);
        Swal.fire("Error", "Failed to load subjects", "error");
      })
      .finally(() => setLoading(false));
  }, [studentClass, selectedTeacherId]);

  
  useEffect(() => {
    if (!studentClass || !selectedTeacherId || !selectedSubject) return;

    setLoading(true);
    setQuestions([]);

    getQuestionPaper({
      studentClass,
      teacherId: selectedTeacherId,
      subject: selectedSubject,
    })
      .then((data) => {
        setQuestions(data);

        if (!data || data.length === 0) {
          Swal.fire({
            icon: "info",
            title: "Question Paper Not Found",
            text: "No question paper available for the selected Class, Teacher, and Subject.",
            confirmButtonText: "OK",
          });
        } else {
          toast.success("Question paper loaded successfully");
        }
      })

      .catch((err) => {
        setError(err.message);
        Swal.fire("Error", "Failed to load questions", "error");
      })
      .finally(() => setLoading(false));
  }, [studentClass, selectedTeacherId, selectedSubject]);

  return (
    <div className="qbank-root">
      <Header />
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

    
      <ToastContainer position="top-center" autoClose={2000} />

      <header className="qb-header">
        <div className="qb-title-wrap">
          <button
            className="qb-menu-icon"
            onClick={() => setSidebarOpen((p) => !p)}
            aria-label="Toggle sidebar"
          >
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 14">
              <rect width="20" height="2" rx="1" />
              <rect y="6" width="12" height="2" rx="1" />
              <rect y="12" width="20" height="2" rx="1" />
            </svg>
          </button>
          <span className="qb-title">Question Bank</span>
        </div>

        <div className="qb-controls">
          <label className="qb-select-wrap">
            <select value={studentClass} onChange={(e) => setStudentClass(e.target.value)}>
              <option value="">Select Class</option>
              {classes.map((cls) => (
                <option key={cls} value={cls}>
                  {cls}
                </option>
              ))}
            </select>
          </label>

          <label className="qb-select-wrap">
            <select
              value={selectedTeacherId}
              onChange={(e) => setSelectedTeacherId(e.target.value)}
              disabled={!teachers.length}
            >
              <option value="">Select Teacher</option>
              {teachers.map((t) => (
                <option key={t.teacherId} value={t.teacherId}>
                  {t.teacherName}
                </option>
              ))}
            </select>
          </label>

          <label className="qb-select-wrap">
            <select
              value={selectedSubject}
              onChange={(e) => setSelectedSubject(e.target.value)}
              disabled={!subjects.length}
            >
              <option value="">Select Subject</option>
              {subjects.map((sub) => (
                <option key={sub} value={sub}>
                  {sub}
                </option>
              ))}
            </select>
          </label>
        </div>
      </header>

      <main className="qb-container">
        <div className="qb-wrapper">
          <div className="qb-top">
            <h2 className="qb-course">{selectedSubject || "Course"}</h2>
            <div className="qb-meta">{questions.length} Questions</div>
          </div>

          {loading && <div className="qb-loading">Loading...</div>}
          {error && <div className="qb-error">{error}</div>}

          <div className="qb-cards">
            {!loading && !questions.length && (
              <div className="qb-empty">No questions available</div>
            )}

            {questions.map((q, i) => (
              <article key={i} className="qb-card">
                <div className="qb-card-content">
                  <div className="qb-index">{i + 1}.</div>

                  <div className="qb-body">
                    <div className="qb-text">{q.questionText}</div>

                    <form className="qb-options">
                      {[q.options1, q.options2, q.options3, q.options4]
                        .filter(Boolean)
                        .map((opt, idx) => (
                          <label key={idx} className="qb-option">
                            <span className="qb-option-text">{opt}</span>
                          </label>
                        ))}
                    </form>

                    <div className="qb-pill-row">
                      <span className="qb-difficulty">{q.title}</span>
                    </div>
                  </div>
                </div>
              </article>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}
