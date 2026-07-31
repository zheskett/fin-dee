import express, {
  type Application,
  type Request,
  type Response,
} from "express";
import cors from "cors";
import * as api from "@actual-app/api";

const app: Application = express();
const port = 5008;

app.use(cors());

app.post("/sync", async (_req: Request, res: Response) => {
  try {
    await api.init({
      serverURL: process.env.ACTUAL_SERVER_URL!,
      password: process.env.ACTUAL_PASSWORD!,
      dataDir: "/data",
    });

    console.log("Downloading Budget");
    await api.downloadBudget(process.env.SYNC_ID!);
    console.log("Syncing Banks...");
    await api.runBankSync();
    await api.shutdown();

    res.status(200).json({ message: "Sync Complete" });
  } catch (e: any) {
    console.error(e);
    res.status(500).json({ error: e });
  }
});

app.listen(port, () =>
  console.log("Sync Microservice: Listening on port %d", port),
);
