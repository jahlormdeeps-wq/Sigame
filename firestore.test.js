const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS & PILLARS TESTS ---

test("Unauthenticated user: cannot read products or orders", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("orders").get());
  await assertFails(unauthDb.collection("products").get());
});

test("Authenticated user: can read products catalog", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("products").doc("prod_test_1").set({
      id: "prod_test_1",
      sellerId: "seller_test",
      sellerUid: ALICE_UID,
      title: "Bonwire Kente Cloth",
      categoryId: "fashion_beauty",
      priceGhs: 1200.0,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("products").doc("prod_test_1").get());
});

test("Authenticated user: can create their own order", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").doc("order_123").set({
      id: "order_123",
      orderNumber: "ASI-GH-1234",
      customerUid: ALICE_UID,
      customerName: "Alice Mensah",
      customerPhone: "+233241234567",
      totalGhs: 450.0,
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot create order with spoofed customerUid", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("orders").doc("order_spoofed").set({
      id: "order_spoofed",
      orderNumber: "ASI-GH-9999",
      customerUid: BOB_UID, // Spoofed!
      customerName: "Bob",
      customerPhone: "+233201234567",
      totalGhs: 100.0,
      createdAt: new Date(),
    })
  );
});

test("Cross-user isolation: Bob cannot read Alice's order", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("alice_order").set({
      id: "alice_order",
      orderNumber: "ASI-GH-5555",
      customerUid: ALICE_UID,
      customerName: "Alice Mensah",
      customerPhone: "+233241234567",
      totalGhs: 300.0,
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("orders").doc("alice_order").get());
});

test("Query alignment: Alice can query her own orders with customerUid filter", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("orders").doc("alice_order").set({
      id: "alice_order",
      orderNumber: "ASI-GH-5555",
      customerUid: ALICE_UID,
      customerName: "Alice Mensah",
      customerPhone: "+233241234567",
      totalGhs: 300.0,
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("orders").where("customerUid", "==", ALICE_UID).get()
  );
});

test("Query alignment: Querying orders without customerUid filter fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("orders").get());
});
