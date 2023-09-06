/**
=========================================================
* Material Dashboard 2 React - v2.2.0
=========================================================

* Product Page: https://www.creative-tim.com/product/material-dashboard-react
* Copyright 2023 Creative Tim (https://www.creative-tim.com)

Coded by www.creative-tim.com

 =========================================================

* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
*/

// react-router-dom components
import { Link, useNavigate } from "react-router-dom";

// @mui material components
import Card from "@mui/material/Card";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";
import MDTypography from "components/MDTypography";
import MDInput from "components/MDInput";
import MDButton from "components/MDButton";

// Authentication layout components
import CoverLayout from "layouts/authentication/components/CoverLayout";

// Images
import bgImage from "assets/images/bg-sign-up-cover.jpeg";
import { useState } from "react";
import { API_BASE_URL } from "util/host-utils";

function Cover() {
  const redirection = useNavigate();
  //닉네임 중복체크 유무
  const [nickCheck, setNickCheck] = useState(false);

  //이메일 버튼 클릭
  const [emailBtn, setEmailBtn] = useState(false);

  //이메일 인증 유무
  const [emailCheck, setEmailCheck] = useState(false);

  //상태변수로 회원가입 입력값 관리
  const [userValue, setUserValue] = useState({
    nickName: "",
    password: "",
    userId: "",
    checkPassword: "",
  });

  //검증 메세지에 대한 상태변수 관리
  const [message, setMessage] = useState({
    nickName: "",
    password: "",
    checkPassword: "",
    userId: "",
  });

  //검증 완료 체크에 대한 상태변수 관리
  const [correct, setCorrect] = useState({
    nickName: false,
    password: false,
    checkPassword: false,
    userId: false,
  });

  //검증 데이터를 상태변수에 저장하는 함수
  const saveInputState = ({ key, inputVal, flag, msg }) => {
    inputVal !== "pass" &&
      setUserValue({
        ...userValue,
        [key]: inputVal,
      });

    setCorrect({
      ...correct,
      [key]: flag,
    });

    setMessage({
      ...message,
      [key]: msg,
    });
  };

  // 닉네임 입력창 체인지 이벤트 핸들러
  const nickHandler = (e) => {
    const inputVal = e.target.value;
    // 닉네임 입력값 검증(특수문자 x 2~12자리)
    const idRegex = /^[ㄱ-ㅎ가-힣a-z0-9-_]{2,12}$/;

    // 닉네임 변경 시 중복 체크 초기화
    setNickCheck(false);
    let flag = false;
    setCorrect({ ...correct, nickName: flag });

    let msg;
    if (!inputVal) {
      msg = "닉네임은 필수값입니다.";
    } else if (!idRegex.test(inputVal)) {
      msg = "닉네임은 특수문자를 제외한 2~12자리여야 합니다.";
    } else if (!nickCheck) {
      msg = "닉네임 중복체크 버튼을 클릭하세요";
      flag = true;
    }

    saveInputState({
      key: "nickName",
      inputVal,
      msg,
      flag,
    });
  };

  // 닉네임 중복체크 버튼 클릭
  const duplicateCheck = (e) => {
    e.preventDefault();
    let msg,
      flag = false;
    if (!correct.nickName) {
      msg = "닉네임 먼저 입력해주세요";
      setMessage({ ...message, nickName: msg });
      return;
    }
    fetch(`${API_BASE_URL}/api/v1/user/nickNameDuplicateCheck`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ nickName: userValue.nickName }),
    })
      .then((res) => {
        // 200 : 사용가능, 409 : 중복된 아이디
        if (res.status === 200 || res.status === 409) {
          return res.json();
        } else {
          alert("서버와 통신이 원활하지 않습니다.");
          return;
        }
      })
      .then((json) => {
        console.log(json);
        msg = json.message;
        setNickCheck(true);
        setMessage({ ...message, nickName: msg });
      })
      .catch((err) => {
        console.error("에러 ", err);
        alert("서버와 통신이 원활하지 않습니다.");
      });
  };

  //패스워드 입력창 체인지 이벤트 핸들러
  const passwordHandler = (e) => {
    const inputVal = e.target.value;

    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[$@$!%*#?&])[A-Za-z\d$@$!%*#?&]{8,20}$/;

    let msg,
      flag = false;
    if (!inputVal) {
      //비밀번호 안적음
      msg = "비밀번호는 필수입니다.";
    } else if (!pwRegex.test(inputVal)) {
      msg = "8~20 글자 영문,특수문자를 포함해 주세요.";
    } else {
      msg = "사용 가능한 비밀번호입니다.";
      flag = true;
      setCorrect({ ...correct, password: flag });
    }

    saveInputState({
      key: "password",
      inputVal,
      msg,
      flag,
    });
  };

  //패스워드 체크입력창 체인지 이벤트 핸들러
  const pwCheckHandler = (e) => {
    let inputVal = e.target.value;
    //검증 시작
    let msg,
      flag = false;
    if (!e.target.value) {
      msg = "비밀번호 확인란은 필수입니다.";
    } else if (userValue.password !== e.target.value) {
      msg = "패스워드가 일치하지 않습니다.";
    } else {
      msg = "패스워드가 일치합니다.";
      flag = true;
    }

    saveInputState({
      key: "checkPassword",
      inputVal,
      msg,
      flag,
    });
  };

  //이메일 입력창 체인지 이벤트 핸들러
  const emailHandler = (e) => {
    const inputVal = e.target.value;

    const emailRegex =
      /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;

    let msg,
      flag = false;
    if (!inputVal) {
      msg = "이메일은 필수값입니다.";
    } else if (!emailRegex.test(inputVal)) {
      msg = "이메일 형식이 아닙니다.";
    } else {
      msg = "이메일 인증하기 버튼을 눌러주세요";
      flag = true;
    }
    saveInputState({
      key: "userId",
      inputVal,
      msg,
      flag,
    });
  };

  //이메일 인증하기 버튼 클릭
  const mailSend = (e) => {
    e.preventDefault();
    let msg,
      flag = false;
    if (!correct.userId) {
      msg = "이메일 먼저 입력해주세요";
      setMessage({ ...message, userId: msg });
      return;
    }
    fetch(`${API_BASE_URL}/api/v1/user/userIdDuplicateCheck`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify({ userId: userValue.userId }),
    })
      .then((res) => {
        if (res.status === 200 || res.status === 409) {
          return res.json();
        } else {
          alert("서버와 통신이 원활하지 않습니다.");
          return;
        }
      })
      .then((json) => {
        console.log(json);
        msg = json.message;
        setMessage({ ...message, userId: msg });
        flag = true;
      })
      .catch((err) => {
        console.error("에러 ", err);
        alert("서버와 통신이 원활하지 않습니다.");
      });
    setEmailBtn(flag);
  };

  //4개의 입력칸이 모두 검증에 통과했는지 여부를 검사
  const isValid = () => {
    for (const key in correct) {
      if (!correct[key]) {
        return false;
      }
    }
    return true;
  };

  // 회원가입 요청 함수
  const fetchJoin = async () => {
    const res = await fetch(`${API_BASE_URL}/api/v1/user/signUp`, {
      method: "POST",
      headers: { "content-type": "application/json" },
      body: JSON.stringify(userValue),
    }).catch((err) => {
      console.error("에러 ", err);
    });

    //잘못된 요청시 경고창 띄움
    if (res.status !== 200) {
      const json = res.json();
      if (json.errorCode === 1002) {
        alert("이메일이 중복 되었습니다.");
      }
      if (json.errorCode === 1003) {
        alert("닉네임이 중복 되었습니다.");
      } else {
        alert("서버와 통신이 원활하지 않습니다.");
      }
      return;
    }

    if (res.status === 200) {
      alert("회원가입에 성공했습니다.");
      redirection("/authentication/sign-in");
    }
  };

  //회원가입 버튼 클릭시 이벤트
  const joinHandler = (e) => {
    e.preventDefault();
    if (!isValid()) {
      alert("입력란을 다시 확인해 주세요");
      return;
    }
    if (!nickCheck) {
      alert("닉네임 중복 체크를 진행해주세요.");
      return;
    }
    // if (!emailCheck) {
    //   alert("이메일 인증을 진행해주세요.");
    //   return;
    // }
    else {
      fetchJoin();
    }
  };

  return (
    <CoverLayout image={bgImage}>
      <Card>
        <MDBox
          variant="gradient"
          bgColor="info"
          borderRadius="lg"
          coloredShadow="success"
          mx={2}
          mt={-3}
          p={3}
          mb={1}
          textAlign="center"
        >
          <MDTypography variant="h4" fontWeight="medium" color="white" mt={1}>
            회원가입
          </MDTypography>
          <MDTypography display="block" variant="button" color="white" my={1}>
            Enter your email and password to register
          </MDTypography>
        </MDBox>
        <MDBox pt={4} pb={3} px={3}>
          <MDBox component="form" role="form">
            <MDBox mb={2} display="flex">
              <MDBox width={"80%"}>
                <MDInput
                  type="text"
                  label="NickName"
                  variant="standard"
                  onChange={nickHandler}
                  fullWidth
                />
              </MDBox>
              <MDButton variant="gradient" color="info" size="small" onClick={duplicateCheck}>
                중복체크
              </MDButton>
            </MDBox>
            <span style={{ color: correct.nickName ? "green" : "red", fontSize: "1rem" }}>
              {message.nickName}
            </span>
            <MDBox mb={2} display="flex">
              <MDBox width={"80%"}>
                <MDInput
                  type="email"
                  label="Email"
                  variant="standard"
                  onChange={emailHandler}
                  fullWidth
                />
              </MDBox>
              <MDButton variant="gradient" color="info" size="small" onClick={mailSend}>
                인증하기
              </MDButton>
            </MDBox>
            <MDBox mb={2} style={{ display: emailBtn ? "flex" : "none" }}>
              <MDBox width={"80%"}>
                <MDInput type="text" label="Code" variant="standard" fullWidth />
              </MDBox>
              <MDButton variant="gradient" color="info" size="small">
                인증확인
              </MDButton>
            </MDBox>
            <span style={{ color: correct.userId ? "green" : "red", fontSize: "1rem" }}>
              {message.userId}
            </span>
            <MDBox mb={2}>
              <MDInput
                type="password"
                label="Password"
                variant="standard"
                onChange={passwordHandler}
                fullWidth
              />
            </MDBox>
            <span style={{ color: correct.password ? "green" : "red", fontSize: "1rem" }}>
              {message.password}
            </span>
            <MDBox mb={2}>
              <MDInput
                type="password"
                label="PasswordCheck"
                variant="standard"
                onChange={pwCheckHandler}
                fullWidth
              />
            </MDBox>
            <span style={{ color: correct.checkPassword ? "green" : "red", fontSize: "1rem" }}>
              {message.checkPassword}
            </span>
            <MDBox mt={4} mb={1}>
              <MDButton variant="gradient" color="info" onClick={joinHandler} fullWidth>
                가입하기
              </MDButton>
            </MDBox>
            <MDBox mt={3} mb={1} textAlign="center">
              <MDTypography variant="button" color="text">
                이미 회원이신가요?{" "}
                <MDTypography
                  component={Link}
                  to="/authentication/sign-in"
                  variant="button"
                  color="info"
                  fontWeight="medium"
                  textGradient
                >
                  로그인 페이지로
                </MDTypography>
              </MDTypography>
            </MDBox>
          </MDBox>
        </MDBox>
      </Card>
    </CoverLayout>
  );
}

export default Cover;
