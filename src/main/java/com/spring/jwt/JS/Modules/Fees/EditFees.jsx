import React, { useState, useEffect } from "react";
import "./EditFees.css";
import Header from "../../Components/Header/Header";
import Sidebar from "../../Components/SideBar/SideBar";
import { FaEdit, FaTrash } from "react-icons/fa";
import FeesApi from "../../service/FeesApi/EditFeesApi";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Swal from "sweetalert2";
 
const EditFees = () => {
  const [formData, setFormData] = useState({
    feesId: "",
    name: "",
    feeAmount: "0",
    type: "",
    className: "",
    status: "",
    date: "",
    batch: "",
    userId: "",
  });
 
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [feesList, setFeesList] = useState([]);
  const [filteredList, setFilteredList] = useState([]);
  const [isEditing, setIsEditing] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
 
  const fetchAllFees = async () => {
    try {
      const response = await FeesApi.getAllFees();
      const list = Array.isArray(response.data)
        ? response.data
        : Array.isArray(response.data?.data)
        ? response.data.data
        : Array.isArray(response.data?.result)
        ? response.data.result
        : [];
      setFeesList(list);
      setFilteredList(list);
    } catch (error) {
      console.error("Error fetching fees:", error);
      setFeesList([]);
      setFilteredList([]);
    }
  };
 
  useEffect(() => {
    fetchAllFees();
  }, []);
 
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };
 
  const handleSubmit = async (e) => {
    e.preventDefault();
if (
  !formData.name ||
  !formData.feeAmount ||
  !formData.type ||
  !formData.className ||
  !formData.status ||
  !formData.date ||
  !formData.batch
) {
  toast.warning("Please fill all required fields!");
  return;
}
 
    const payload = {
      feesId: formData.feesId ? parseInt(formData.feesId) : undefined,
      name: formData.name,
      fee: parseFloat(formData.feeAmount),
      type: formData.type,
      studentClass: formData.className,
      status: formData.status,
      date: formData.date,
      batch: formData.batch,
      userId: formData.userId,
     
    };
 
    try {
      if (isEditing) {
        await FeesApi.updateFees(formData.feesId, payload);
        toast.success("Fees updated successfully!");
      } else {
        await FeesApi.addFees(payload);
        toast.success("Fees added successfully!");
      }
 
      fetchAllFees();
      setFormData({
        feesId: "",
        name: "",
        feeAmount: "0",
        type: "",
        className: "",
        status: "",
        date: "",
        batch: "",
        userId: "",
      });
      setIsEditing(false);
    } catch (error) {
      console.error("Error saving fees:", error);
      toast.error("Failed to save fees.");
    }
  };
 
  const handleEditClick = (fee) => {
    setFormData({
      feesId: fee.feesId,
      name: fee.name,
      feeAmount: fee.fee,
      type: fee.type,
      className: fee.studentClass,
      status: fee.status,
      date: fee.date,
      batch: fee.batch,
      userId: fee.userId || "",
    });
    setIsEditing(true);
  };
 
  const handleDeleteClick = async (feesId) => {
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "You want to delete this fee record!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Yes, delete it!",
      cancelButtonText: "Cancel",
    });
 
    if (!result.isConfirmed) return;
 
    try {
      await FeesApi.deleteFees(feesId);
      toast.success("Record deleted successfully!");
      fetchAllFees();
    } catch (error) {
      console.error("Error deleting record:", error);
      toast.error("Failed to delete record.");
    }
  };
  useEffect(() => {
    const query = searchTerm.toLowerCase().trim();
    if (!query) {
      setFilteredList(feesList);
      return;
    }
 
    setFilteredList(
      feesList.filter(
        (item) =>
          item.name?.toLowerCase().includes(query) ||
          item.status?.toLowerCase().includes(query) ||
          item.feesId?.toString().includes(query)
      )
    );
  }, [searchTerm, feesList]);
 
  return (
    <div className="edit-fees-page">
      <Header />
 
      <ToastContainer
        position="top-center"
        autoClose={2000}
        hideProgressBar={false}
        closeOnClick
        pauseOnHover
        draggable
        theme="colored"
      />
 
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
 
      <div className="edit-fees-header">
        <div className="edit-fees-header-left">
          <button
            className="hamburger"
            aria-label="menu"
            onClick={() => setSidebarOpen(!sidebarOpen)}
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
          <h2>{isEditing ? "Update Fees" : "Add Fees"}</h2>
        </div>
      </div>
 
      <div className="edit-fees-container">
        <form className="edit-fees-form" onSubmit={handleSubmit}>
          <div className="edit-fees-form-grid">
            <div className="edit-fees-form-group">
              <label>Fees ID</label>
              <input
                type="number"
                name="feesId"
                placeholder="Fees ID"
                value={formData.feesId}
                onChange={handleChange}
                disabled
              />
            </div>
 
            <div className="edit-fees-form-group">
              <label>Name</label>
              <input
               
                type="text"
                name="name"
                placeholder="Enter Name"
                value={formData.name}
                onChange={handleChange}
              />
            </div>
 
            <div className="edit-fees-form-group">
              <label>Fee Amount</label>
              <input
                type="number"
                name="feeAmount"
             
                value={formData.feeAmount}
                onChange={handleChange}
              />
            </div>
 
            <div className="edit-fees-form-group">
              <label>Type</label>
              <input
                type="text"
                name="type"
                placeholder="Enter Type"
                value={formData.type}
                onChange={handleChange}
               
              />
            </div>
 
            <div className="edit-fees-form-group">
              <label>Class</label>
              <input
                type="text"
                name="className"
                placeholder="Enter Class"
                value={formData.className}
                onChange={handleChange}
             
              />
            </div>
 
            <div className="edit-fees-form-group">
              <label>Status</label>
              <input
                type="text"
                name="status"
                placeholder="Enter Status"
                value={formData.status}
                onChange={handleChange}
               
              />
            </div>
 
            <div className="edit-fees-form-group">
              <label>Date</label>
              <input
                type="date"
                name="date"
                value={formData.date}
                onChange={handleChange}
               
              />
            </div>
 
            <div className="edit-fees-form-group">
              <label>Batch</label>
              <input
                type="text"
                name="batch"
                placeholder="Enter Batch"
                value={formData.batch}
                onChange={handleChange}
              />
            </div>
          </div>
 
          <div className="edit-fees-actions">
            <button type="submit" className="edit-fees-submit-btn">
              {isEditing ? "Update Fees" : "Save Fees"}
            </button>
 
            <div className="search-area-below">
              <input
                type="text"
                placeholder="Search by Name, ID, or Status"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
        </form>
 
        <div className="edit-fees-card edit-fees-table-card">
          <div className="edit-fees-table-wrap">
            <table className="edit-fees-table">
              <thead>
                <tr>
                  <th>Fees ID</th>
                  <th>Name</th>
                  <th>Fee Amount</th>
                  <th>Type</th>
                  <th>Class</th>
                  <th>Status</th>
                  <th>Date</th>
                  <th>Batch</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {!Array.isArray(filteredList) || filteredList.length === 0 ? (
                  <tr>
                    <td colSpan="9" style={{ textAlign: "center" }}>
                      No data found.
                    </td>
                  </tr>
                ) : (
                  filteredList.map((f, i) => (
                    <tr key={i}>
                      <td>{f.feesId}</td>
                      <td>{f.name}</td>
                      <td>{f.fee}</td>
                      <td>{f.type}</td>
                      <td>{f.studentClass}</td>
                      <td>
                        <span
                          className={
                            f.status?.toLowerCase() === "active"
                              ? "status-active"
                              : f.status?.toLowerCase() === "inactive"
                              ? "status-inactive"
                              : ""
                          }
                        >
                          {f.status}
                        </span>
                      </td>
 
                      <td>{f.date}</td>
                      <td>{f.batch}</td>
                      <td className="action-buttons">
                        <button
                          className="edit-icon-btn"
                          type="button"
                          onClick={() => handleEditClick(f)}
                        >
                          <FaEdit />
                        </button>
                        <button
                          className="delete-icon-btn"
                          type="button"
                          onClick={() => handleDeleteClick(f.feesId)}
                        >
                          <FaTrash />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
};
 
export default EditFees;