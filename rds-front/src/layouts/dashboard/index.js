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

// @mui material components
import Grid from "@mui/material/Grid";

// Material Dashboard 2 React components
import MDBox from "components/MDBox";

// Material Dashboard 2 React example components
import DashboardLayout from "examples/LayoutContainers/DashboardLayout";
import DashboardNavbar from "examples/Navbars/DashboardNavbar";
import Footer from "examples/Footer";
import ReportsBarChart from "examples/Charts/BarCharts/ReportsBarChart";
import ReportsLineChart from "examples/Charts/LineCharts/ReportsLineChart";
import ComplexStatisticsCard from "examples/Cards/StatisticsCards/ComplexStatisticsCard";

// Data
import reportsBarChartData from "layouts/dashboard/data/reportsBarChartData";
import reportsLineChartData from "layouts/dashboard/data/reportsLineChartData";

// Dashboard components
import Projects from "layouts/dashboard/components/Projects";
import OrdersOverview from "layouts/dashboard/components/OrdersOverview";
import MDButton from "components/MDButton";
import { API_BASE_URL } from "util/host-utils";
import { useState } from "react";

function Dashboard() {
  const { sales, tasks } = reportsLineChartData;

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

  // 로그인 토큰 정보 얻어오기
  const getLoginUserInfo = () => {
    return {
      accessToken: localStorage.getItem("ACCESS_TOKEN"),
      refreshToken: localStorage.getItem("REFRESH_TOKEN"),
      nickName: localStorage.getItem("LOGIN_USERNAME"),
    };
  };

  //토큰 및 로그인 유저 데이터를 브라우저에 저장하는 함수
  const setLoginUserInfo = ({ accessToken, refreshToken }, nickName) => {
    localStorage.setItem("ACCESS_TOKEN", accessToken);
    localStorage.setItem("REFRESH_TOKEN", refreshToken);
    if (nickName) {
      localStorage.setItem("LOGIN_USERNAME", nickName);
    }
  };

  //토큰 인증 테스트
  const testHandler = async () => {
    const { accessToken, refreshToken } = getLoginUserInfo();

    await fetch(`${API_BASE_URL}/api/v1/user/test`, {
      method: "GET",
      headers: {
        "content-type": "application/json",
        ACCESS_TOKEN: "Bearer " + accessToken,
        REFRESH_TOKEN: "Bearer " + refreshToken,
      },
    })
      .then((res) => res.json())
      .then((json) => {
        console.log(json);
        alert(json.message);
        if (json.errorCode === 3000) {
          updateToken();
          return;
        }
        if (json.errorCode === 3002) {
          redirection("/authentication/sign-in");
          return;
        }
        // if (json.errorCode === 0) setLoginUserInfo(json.data.tokenBox);
        else {
          alert("알수 없는 오류가 발생하였습니다. 관리자에게 문의하세요");
        }
      })
      .catch((err) => {
        console.log("에러", err);
        alert("서버와 통신이 원활하지 않습니다.");
      });
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
    <DashboardLayout>
      <DashboardNavbar />
      <MDButton onClick={testHandler}>test</MDButton>
      <MDButton onClick={getIp}>test2</MDButton>
      <MDBox py={3}>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="dark"
                icon="weekend"
                title="Bookings"
                count={281}
                percentage={{
                  color: "success",
                  amount: "+55%",
                  label: "than lask week",
                }}
              />
            </MDBox>
          </Grid>
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                icon="leaderboard"
                title="Today's Users"
                count="2,300"
                percentage={{
                  color: "success",
                  amount: "+3%",
                  label: "than last month",
                }}
              />
            </MDBox>
          </Grid>
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="success"
                icon="store"
                title="Revenue"
                count="34k"
                percentage={{
                  color: "success",
                  amount: "+1%",
                  label: "than yesterday",
                }}
              />
            </MDBox>
          </Grid>
          <Grid item xs={12} md={6} lg={3}>
            <MDBox mb={1.5}>
              <ComplexStatisticsCard
                color="primary"
                icon="person_add"
                title="Followers"
                count="+91"
                percentage={{
                  color: "success",
                  amount: "",
                  label: "Just updated",
                }}
              />
            </MDBox>
          </Grid>
        </Grid>
        <MDBox mt={4.5}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6} lg={4}>
              <MDBox mb={3}>
                <ReportsBarChart
                  color="info"
                  title="website views"
                  description="Last Campaign Performance"
                  date="campaign sent 2 days ago"
                  chart={reportsBarChartData}
                />
              </MDBox>
            </Grid>
            <Grid item xs={12} md={6} lg={4}>
              <MDBox mb={3}>
                <ReportsLineChart
                  color="success"
                  title="daily sales"
                  description={
                    <>
                      (<strong>+15%</strong>) increase in today sales.
                    </>
                  }
                  date="updated 4 min ago"
                  chart={sales}
                />
              </MDBox>
            </Grid>
            <Grid item xs={12} md={6} lg={4}>
              <MDBox mb={3}>
                <ReportsLineChart
                  color="dark"
                  title="completed tasks"
                  description="Last Campaign Performance"
                  date="just updated"
                  chart={tasks}
                />
              </MDBox>
            </Grid>
          </Grid>
        </MDBox>
        <MDBox>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6} lg={8}>
              <Projects />
            </Grid>
            <Grid item xs={12} md={6} lg={4}>
              <OrdersOverview />
            </Grid>
          </Grid>
        </MDBox>
      </MDBox>
      <Footer />
    </DashboardLayout>
  );
}

export default Dashboard;
