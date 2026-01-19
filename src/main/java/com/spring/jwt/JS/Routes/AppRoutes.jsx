import React from "react";
import { Routes, Route } from "react-router-dom";
import Dashboard from "../Modules/Dashboard/DashBoard";
import Calendar from "../Modules/Calender/Calendar";
import Fees from "../Modules/Fees/Fees";
import Profile from "../Modules/Profile/Profile";
import List from "../Modules/Teacher/List/List";
import Papers from "../Modules/Teacher/Papers/Papers";
import QuestionBank from "../Modules/Teacher/QuestionBank/QuestionBank";
import Login from "../Modules/Login/Login";
import Exam from "../Modules/Students/Exams/Exams";
import Attendence from "../Modules/Students/Attendence/Attendence";
import Results from "../Modules/Students/Results/Results";
import Paper from "../Modules/Papers/Paper";
import PaperPattern from "../Modules/PaperPattern/PaperPattern";
import EditFees from "../Modules/Fees/EditFees";
import AttendanceSummary from "../Modules/Teacher/Attendance/AttendanceSummary";
import SalaryStructure from "../Modules/Teacher/Salary/SalaryStructure";
import MonthlySalary from "../Modules/Teacher/Salary/MonthlySalary";
import Salary from "../Modules/Teacher/Salary/Salary";

// import DetailedResult from "../Modules/Students/Results/DetailedResult";

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      {/* Dashboard */}
      <Route path="/dashboard" element={<Dashboard />} />

      <Route path="/calender" element={<Calendar />} />

      {/* Teachers */}
      <Route path="/teacher/list" element={<List />} />
      <Route path="/teacher/papers" element={<Papers />} />
      <Route path="/teacher/question-bank" element={<QuestionBank />} />
       <Route path="/teacher/salary" element={<Salary />} />
      <Route path="/teacher/salary/add" element={<SalaryStructure />} />
       <Route path="/teacher/salary/monthly" element={<MonthlySalary />} />
      {/* <Route path="/teacher/salary/edit/:id" element={<AddSalary />} /> */}
      <Route path="/teacher/attendance-summary" element={<AttendanceSummary/>} />
      {/* Students */}
      <Route path="/students/attendence" element={<Attendence />} />
      <Route path="/students/exams" element={<Exam />} />
      <Route path="/students/results" element={<Results />} />
      {/* <Route path="/students/results/detailedresults" element={<DetailedResult />} /> */}

      <Route path="/fees" element={<Fees />} />
      <Route path="/fees/edit" element={<EditFees />} />

      <Route path="/papers" element={<Paper />} />
      <Route path="/papers/paper-patterns" element={<PaperPattern />} />
      <Route path="/profile" element={<Profile />} />
      
      {/* Default redirect */}
      <Route path="*" element={<Dashboard />} />
    </Routes>
  );
};

export default AppRoutes;
