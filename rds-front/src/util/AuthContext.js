import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";

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
    setIsLoggedIn(true);
    setUserName(nickName);
  };

  //토큰 및 로그인 유저 데이터를 브라우저에 저장하는 함수
  const setLoginUserInfo = ({ accessToken, refreshToken }, nickName) => {
    localStorage.setItem("ACCESS_TOKEN", accessToken);
    localStorage.setItem("REFRESH_TOKEN", refreshToken);
    localStorage.setItem("LOGIN_USERNAME", nickName);
  };

  //토큰 유효기간 확인 및 재요청
  const updateToken = async () => {
    const { accessToken, refreshToken } = getLoginUserInfo();

    const res = await fetch(`${API_BASE_URL}/api/vi/updateToken`, {
      method: "GET",
      headers: {
        "content-type": "application/json",
        ACCESS_TOKEN: accessToken,
        REFRESH_TOKEN: refreshToken,
      },
    }).catch((err) => {
      console.log("에러", err);
    });

    if (res.status !== 200) {
      alert(await res.json().message);
      logoutHandler();
    }
    if (res.status === 200) {
      const json = await res.json();
      setLoginUserInfo(json.data.tokenBox);
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
