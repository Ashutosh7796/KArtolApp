import React, { useState, useEffect } from "react";
import { FaEdit, FaTrash } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import Header from "../../../Components/Header/Header";
import Sidebar from "../../../Components/SideBar/SideBar";
import "./SalaryStructure.css";
import { ToastContainer, toast } from "react-toastify";
import Swal from "sweetalert2";
import SalaryStructureApi from "../../../service/TeacherApi/SalaryStructureApi";

export default function SalaryStructure() {
  const [form, setForm] = useState({
    teacherId: "",
    teacherName: "",
    perDaySalary: "",
    annualSalary: "",
  });

  const [editStatus, setEditStatus] = useState("");
  const [records, setRecords] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(5);

  const navigate = useNavigate();
  const handleToggle = () => setSidebarOpen(!sidebarOpen);

 
  useEffect(() => {
    fetchTeachers();
    fetchSalaryStructures();
  }, []);

  const fetchTeachers = async () => {
    const res = await SalaryStructureApi.getAllTeachers();
    setTeachers(res.data.data || []);
  };

  const fetchSalaryStructures = async () => {
    const res = await SalaryStructureApi.getSalaryStructures();
    setRecords(res.data.data || []);
  };

  
  const handleTeacherSelect = (e) => {
    const selected = e.target.value;
    const teacher = teachers.find((t) => t.name === selected);
    setForm({
      ...form,
      teacherName: selected,
      teacherId: teacher ? teacher.teacherId : "",
    });
  };

  const handleChange = (e) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  // const handleSave = async () => {
  //   if (!form.teacherId || !form.perDaySalary || !form.annualSalary) {
  //     toast.warning("Please fill all required fields");
  //     return;
  //   }

  //   await SalaryStructureApi.addSalaryStructure({
  //     teacherId: Number(form.teacherId),
  //     perDaySalary: Number(form.perDaySalary),
  //     annualSalary: Number(form.annualSalary),
  //   });

  //   toast.success("Salary Structure Added Successfully!");
  //   fetchSalaryStructures();
  //   setForm({
  //     teacherId: "",
  //     teacherName: "",
  //     perDaySalary: "",
  //     annualSalary: "",
  //   });
  // };


  const handleSave = async () => {
  
  if (!form.teacherId || !form.perDaySalary || !form.annualSalary) {
    toast.warning("Please fill all required fields");
    return;
  }

  
  if (Number(form.perDaySalary) <= 500) {
    toast.warning("Per Day Salary must be greater than 500");
    return;
  }

  
  if (Number(form.annualSalary) <= 100000) {
    toast.warning("Annual Salary must be greater than 1,00,000");
    return;
  }

  await SalaryStructureApi.addSalaryStructure({
    teacherId: Number(form.teacherId),
    perDaySalary: Number(form.perDaySalary),
    annualSalary: Number(form.annualSalary),
  });

  toast.success("Salary Structure Added Successfully!");
  fetchSalaryStructures();

  setForm({
    teacherId: "",
    teacherName: "",
    perDaySalary: "",
    annualSalary: "",
  });
};

  const handleEdit = (item) => {
    setForm({
      teacherId: item.teacherId,
      teacherName: item.teacherName,
      perDaySalary: item.perDaySalary,
      annualSalary: item.annualSalary,
    });
    setEditStatus(item.status || "ACTIVE");
    setIsEditMode(true);
  };

  // const handleUpdate = async () => {
  //   await SalaryStructureApi.updateSalaryStructure(form.teacherId, {
  //     perDaySalary: Number(form.perDaySalary),
  //     annualSalary: Number(form.annualSalary),
  //     status: editStatus,
  //   });

  //   toast.success("Salary Updated Successfully!");
  //   setIsEditMode(false);
  //   fetchSalaryStructures();
  // };


  const handleUpdate = async () => {
  // 🔴 REQUIRED FIELD CHECK
  if (!form.perDaySalary || !form.annualSalary) {
    toast.warning("Please fill all required fields");
    return;
  }

  // 🔴 PER DAY SALARY VALIDATION
  if (Number(form.perDaySalary) <= 500) {
    toast.warning("Per Day Salary must be greater than 500");
    return;
  }

  // 🔴 ANNUAL SALARY VALIDATION
  if (Number(form.annualSalary) <= 100000) {
    toast.warning("Annual Salary must be greater than 1,00,000");
    return;
  }

  await SalaryStructureApi.updateSalaryStructure(form.teacherId, {
    perDaySalary: Number(form.perDaySalary),
    annualSalary: Number(form.annualSalary),
    status: editStatus,
  });

  toast.success("Salary Updated Successfully!");
  setIsEditMode(false);
  fetchSalaryStructures();
};

  const handleDelete = async (teacherId) => {
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "You want to delete this salary structure!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      confirmButtonText: "Yes, delete it!",
    });

    if (!result.isConfirmed) return;

    await SalaryStructureApi.deleteSalaryStructure(teacherId);
    toast.success("Salary structure deleted successfully!");
    fetchSalaryStructures();
  };

  /* ================= PAGINATION ================= */
  const totalPages = Math.ceil(records.length / itemsPerPage);
  const reversedRecords = [...records].reverse();
  const paginatedData = reversedRecords.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  return (
    <div className="salarystructure-layout">
      <Header onToggle={handleToggle} />
      <ToastContainer position="top-center" autoClose={3000} theme="colored" />

      <div className="salarystructure-content">
        <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

        <div className="salarystructure-page-container">
          <h2 className="salarystructure-title">Add Salary Structure</h2>

          {/* ================= FORM CARD ================= */}
          <div className="salarystructure-card salarystructure-form-card">
            <div className="salarystructure-form">
              <div className="salarystructure-field">
                <label className="salarystructure-label">Teacher ID *</label>
                <input className="salarystructure-input" value={form.teacherId} readOnly />
              </div>

              <div className="salarystructure-field">
                <label className="salarystructure-label">Teacher Name *</label>
                <select
                  className="salarystructure-input"
                  value={form.teacherName}
                  onChange={handleTeacherSelect}
                >
                  <option value="">Select Teacher</option>
                  {teachers.map((t) => (
                    <option key={t.teacherId} value={t.name}>
                      {t.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="salarystructure-field">
                <label className="salarystructure-label">Per Day Salary *</label>
                <input
                  className="salarystructure-input"
                  name="perDaySalary"
                  type="number"
                  value={form.perDaySalary}
                  onChange={handleChange}
                />
              </div>

              <div className="salarystructure-field">
                <label className="salarystructure-label">Annual Salary *</label>
                <input
                  className="salarystructure-input"
                  name="annualSalary"
                  type="number"
                  value={form.annualSalary}
                  onChange={handleChange}

                />
              </div>

              {isEditMode && (
                <div className="salarystructure-field">
                  <label className="salarystructure-label">Status *</label>
                  <select
                    className="salarystructure-input"
                    value={editStatus}
                    onChange={(e) => setEditStatus(e.target.value)}
                  >
                    <option value="ACTIVE">ACTIVE</option>
                    <option value="INACTIVE">INACTIVE</option>
                  </select>
                </div>
              )}

              <div className="salarystructure-btnrow">
                <button
                  className="salarystructure-backbtn"
                  onClick={() => navigate("/teacher/salary")}
                >
                  Back to List
                </button>
                <button
                  className="salarystructure-savebtn"
                  onClick={isEditMode ? handleUpdate : handleSave}
                >
                  {isEditMode ? "Update" : "Save"}
                </button>
              </div>
            </div>
          </div>

          {/* ================= TABLE CARD ================= */}
          <div className="salarystructure-card salarystructure-table-card">
            <div className="salarystructure-table">
              <div className="salarystructure-table-wrap">
                <table className="salarystructure-tablebox">
                  <thead>
                    <tr>
                      <th>Sr</th>
                      <th>Teacher ID</th>
                      <th>Teacher Name</th>
                      <th>Per Day</th>
                      <th>Annual</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {paginatedData.map((item, index) => (
                      <tr key={item.teacherId}>
                        <td>{(currentPage - 1) * itemsPerPage + index + 1}</td>
                        <td>{item.teacherId}</td>
                        <td>{item.teacherName}</td>
                        <td>₹{item.perDaySalary}</td>
                        <td>₹{item.annualSalary}</td>
                        <td>
                          <span className={item.status === "ACTIVE" ? "status-active" : "status-inactive"}>
                            {item.status}
                          </span>
                        </td>
                        <td>
                          <div className="salarystructure-actions">
                            <button className="salarystructure-editbtn" onClick={() => handleEdit(item)}>
                              <FaEdit />
                            </button>
                            <button className="salarystructure-deletebtn" onClick={() => handleDelete(item.teacherId)}>
                              <FaTrash />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* PAGINATION */}
              {totalPages > 1 && (
                <div className="salarystructure-pagination">
                  <div className="salarystructure-pagination-controls">
                    <button
                      className="salarystructure-pagination-button"
                      disabled={currentPage === 1}
                      onClick={() => handlePageChange(currentPage - 1)}
                    >
                      Previous
                    </button>

                    <button
                      className="salarystructure-pagination-button"
                      disabled={currentPage === totalPages}
                      onClick={() => handlePageChange(currentPage + 1)}
                    >
                      Next
                    </button>
                  </div>

                  <select
                    className="salarystructure-pagination-select"
                    value={itemsPerPage}
                    onChange={(e) => {
                      setItemsPerPage(Number(e.target.value));
                      setCurrentPage(1);
                    }}
                  >
                    {[5, 10, 20].map((n) => (
                      <option key={n} value={n}>
                        {n} rows
                      </option>
                    ))}
                  </select>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}