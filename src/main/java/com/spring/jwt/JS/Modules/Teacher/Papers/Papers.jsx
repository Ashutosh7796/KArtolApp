import React, { useState, useEffect } from "react";
import "./Papers.css";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";
import PaperDropdownApi from "../../../service/TeacherApi/PaperApi";

import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Swal from "sweetalert2";

export default function Papers() {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const [papers, setPapers] = useState([]);

  const [subjects, setSubjects] = useState([]);
  const [classes, setClasses] = useState([]);
  const [statusOptions, setStatusOptions] = useState([]);

  const [subject, setSubject] = useState("All");
  const [classFilter, setClassFilter] = useState("All");
  const [status, setStatus] = useState("All");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const toggleSidebar = () => setSidebarOpen((s) => !s);
  const closeSidebar = () => setSidebarOpen(false);

  
  useEffect(() => {
    const loadAll = async () => {
      try {
        setLoading(true);
        const res = await PaperDropdownApi.getAllPapers();
        setPapers(res.data?.data || []);
        toast.success("Papers loaded successfully");
      } catch {
        setError("Failed to load papers");
        Swal.fire("Error", "Failed to load papers", "error");
      } finally {
        setLoading(false);
      }
    };

    loadAll();
  }, []);

 
  useEffect(() => {
    PaperDropdownApi.getSubjects()
      .then((res) => {
        setSubjects(res.data?.data || []);
        toast.success("Subjects loaded");
      })
      .catch(() => {
        Swal.fire("Error", "Failed to load subjects", "error");
      });
  }, []);


  useEffect(() => {
    if (subject === "All") {
      setClasses([]);
      setClassFilter("All");
      return;
    }

    PaperDropdownApi.getClasses(subject)
      .then((res) => {
        setClasses(res.data?.data || []);
        toast.success("Classes loaded");
      })
      .catch(() => {
        Swal.fire("Error", "Failed to load classes", "error");
      });
  }, [subject]);

  
  useEffect(() => {
    if (subject === "All" || classFilter === "All") {
      setStatusOptions([]);
      setStatus("All");
      return;
    }

    PaperDropdownApi.getIsLiveOptions(subject, classFilter)
      .then((res) => {
        setStatusOptions(res.data?.data || []);
        toast.success("Status options loaded");
      })
      .catch(() => {
        Swal.fire("Error", "Failed to load status options", "error");
      });
  }, [subject, classFilter]);

  
  useEffect(() => {
    if (subject === "All" || classFilter === "All" || status === "All") {
      return;
    }

    const fetchPapers = async () => {
      try {
        setLoading(true);
        setError(null);

        const res = await PaperDropdownApi.getPapers(
          subject,
          classFilter,
          status.toLowerCase()
        );

        const data = res.data?.data || [];
        setPapers(data);

        if (data.length === 0) {
          Swal.fire({
            icon: "info",
            title: "No Papers Found",
            text: "No papers available for the selected filters",
            confirmButtonText: "OK",
          });
        } else {
          toast.success("Papers loaded successfully");
        }
      } catch {
        setError("Failed to load papers");
        Swal.fire("Error", "Failed to load papers", "error");
      } finally {
        setLoading(false);
      }
    };

    fetchPapers();
  }, [subject, classFilter, status]);

  return (
    <>
      <Header />
      <Sidebar isOpen={sidebarOpen} onClose={closeSidebar} showFloating={false} />

    
      <ToastContainer position="top-center" autoClose={2000} />

      <div className="papers-root">
        <header className="header">
          <div className="title-wrap">
            <button
              className="hamburger"
              aria-label="menu"
              onClick={toggleSidebar}
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="20"
                height="14"
                viewBox="0 0 20 14"
                fill="none"
              >
                <rect width="20" height="2" rx="1" fill="currentColor" />
                <rect y="6" width="12" height="2" rx="1" fill="currentColor" />
                <rect y="12" width="20" height="2" rx="1" fill="currentColor" />
              </svg>
            </button>
            <span className="title">Papers</span>
          </div>

          <div className="controls">
            <label className="select-wrap">
              <select value={subject} onChange={(e) => setSubject(e.target.value)}>
                <option value="All">All Subjects</option>
                {subjects.map((s, i) => (
                  <option key={i} value={s}>{s}</option>
                ))}
              </select>
            </label>

            <label className="select-wrap">
              <select
                value={classFilter}
                onChange={(e) => setClassFilter(e.target.value)}
              >
                <option value="All">All Classes</option>
                {classes.map((c, i) => (
                  <option key={i} value={c}>Class {c}</option>
                ))}
              </select>
            </label>

            <label className="select-wrap">
              <select value={status} onChange={(e) => setStatus(e.target.value)}>
                <option value="All">All</option>
                {statusOptions.map((s, i) => (
                  <option key={i} value={s}>{s}</option>
                ))}
              </select>
            </label>
          </div>
        </header>

        <main className="container">
          {loading ? (
            <p>Loading papers...</p>
          ) : error ? (
            <p style={{ color: "red" }}>{error}</p>
          ) : papers.length === 0 ? (
            <p>No papers found.</p>
          ) : (
            <div className="questions-grid">
              {papers.map((paper, index) => {
                const options = [
                  paper.options1,
                  paper.options2,
                  paper.options3,
                  paper.options4,
                ].filter(Boolean);

                return (
                  <article key={index} className="question-card small-card">
                    <h3>{paper.title}</h3>

                    <p className="q-text">{paper.questionText}</p>

                    <ul className="options-list">
                      {options.map((opt, i) => (
                        <li key={i}>{opt}</li>
                      ))}
                    </ul>

                    <div className="timer">
                      {paper.isLive ? "LIVE" : "PAST"}
                    </div>
                  </article>
                );
              })}
            </div>
          )}
        </main>
      </div>
    </>
  );
}
