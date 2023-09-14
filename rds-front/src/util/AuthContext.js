import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";
import { useNavigate } from "react-router-dom";

const AuthContext = React.createContext({
  isLoggedIn: false, //로그인 했는지의 여부 추적
  userName: "",
  onLogout: () => {}, //더미 함수를 넣으면 자동완성 시 편함.
  onLogin: () => {},
  setUserInfo: () => {},
});

const AuthContextProvider = (props) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userName, setUserName] = useState("");

  const redirection = useNavigate();

  AuthContextProvider.propTypes = {
    children: PropTypes.node.isRequired,
  };

  //컴포넌트가 렌더링 될 때 localStorage에서 로그인 정보를 가지고 와서 상태를 설정.
  useEffect(() => {
    if (localStorage.getItem("autoLogin") === "1" && localStorage.getItem("REFRESH_TOKEN")) {
      setIsLoggedIn(true);
      setUserName(localStorage.getItem("LOGIN_USERNAME"));
      updateToken();
    }
  }, []);

  //로그아웃 핸들러
  const logoutHandler = () => {
    localStorage.clear();
    setIsLoggedIn(false);
  };

  // ip 주소 정보 얻기
  const getIp = async () => {
    let ip;
    await fetch("https://geolocation-db.com/json/", {
      method: "GET",
    })
      .then((res) => res.json())
      .then((json) => {
        console.log(json);
        ip = json.IPv4;
      });
    // os정보얻기
    const userAgent = window.navigator.userAgent;
    console.log(userAgent);
    return { ip, userAgent };
  };

  //로그인 핸들러
  const loginHandler = ({ accessToken, refreshToken }, nickName, rememberMe) => {
    // 자동로그인
    if (rememberMe) {
      localStorage.setItem("autoLogin", "1");
    } else {
      localStorage.removeItem("autoLogin");
    }
    //json에 담긴 인증정보를 클라이언트에 보관
    // 1. 로컬 스토리지 - 브라우저가 종료되어도 보관됨.
    // 2. 세션 스토리지 - 브라우저가 종료되면 사라짐.
    localStorage.setItem("ACCESS_TOKEN", accessToken);
    localStorage.setItem("REFRESH_TOKEN", refreshToken);
    localStorage.setItem("LOGIN_USERNAME", nickName);
    // setIsLoggedIn(true);
    // setUserName(nickName);
  };

  //토큰 및 로그인 유저 데이터를 브라우저에 저장하는 함수
  const setLoginUserInfo = ({ accessToken, refreshToken }, nickName) => {
    localStorage.setItem("ACCESS_TOKEN", accessToken);
    localStorage.setItem("REFRESH_TOKEN", refreshToken);
    if (nickName) {
      localStorage.setItem("LOGIN_USERNAME", nickName);
    }
  };

  // 로그인 토큰 정보 얻어오기
  const getLoginUserInfo = () => {
    return {
      accessToken: localStorage.getItem("ACCESS_TOKEN"),
      refreshToken: localStorage.getItem("REFRESH_TOKEN"),
      nickName: localStorage.getItem("LOGIN_USERNAME"),
    };
  };

  //토큰 유효기간 확인 및 재요청
  const updateToken = async () => {
    const { accessToken, refreshToken } = getLoginUserInfo();
    const { ip, userAgent } = getIp();

    await fetch(`${API_BASE_URL}/api/v1/updateToken`, {
      method: "GET",
      headers: {
        "content-type": "application/json",
        ACCESS_TOKEN: "Bearer " + accessToken,
        REFRESH_TOKEN: "Bearer " + refreshToken,
        "USER-AGENT": userAgent,
        "X-Forwarded-For": ip,
      },
    })
      .then((res) => res.json())
      .then((json) => {
        console.log(json);
        alert(json.message);
        if (json.errorCode === 3001 || json.errorCode === 3006) {
          logoutHandler();
          redirection("/authentication/sign-in");
          return;
        }
        if (json.errorCode === 0) setLoginUserInfo(json.data.tokenBox);
        else {
          alert("알수 없는 오류가 발생하였습니다. 관리자에게 문의하세요");
        }
      })
      .catch((err) => {
        console.log("에러", err);
        alert("서버와 통신이 원활하지 않습니다.");
      });
  };

  return (
    <AuthContext.Provider
      value={{
        isLoggedIn: isLoggedIn,
        userName,
        onLogout: logoutHandler,
        onLogin: loginHandler,
        setUserInfo: setLoginUserInfo,
      }}
    >
      {props.children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
