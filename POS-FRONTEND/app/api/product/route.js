import { proxyRoute } from "@/lib/apiProxy";

export async function POST(req) {
  return proxyRoute(req, "product/list");
}