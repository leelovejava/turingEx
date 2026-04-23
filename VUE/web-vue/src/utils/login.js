import router from "@/router";
export const gotoPage = () => {
  if (!localStorage.getItem("spToken")) {
    router.push("/login");
  } else {
    router.push(
      "/coin/constract/btc?timestamp=1694700784934&RouterName=sustainable"
    );
  }
};
