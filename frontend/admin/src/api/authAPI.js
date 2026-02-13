import backendServer from "./backendServer";

export const loginAdmin = async (adminLoginId, adminPass) => {
  const response = await backendServer.post("/admin/api/admin/login", {
    adminLoginId,
    adminPass,
  });

  return response.data;
};
