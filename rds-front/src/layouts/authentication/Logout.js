import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

const Logout = () => {
  const redirection = useNavigate();

  //로그아웃 핸들러
  const logoutHandler = () => {
    localStorage.clear();
  };

  useEffect(() => {
    logoutHandler();
    redirection("/");
    window.location.reload();
  }, []);

  return <div>로그아웃중입니다</div>;
};

export default Logout;
