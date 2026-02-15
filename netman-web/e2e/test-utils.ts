export function generateUniqueEmail() {
  const timestamp = Date.now();
  const random = Math.floor(Math.random() * 10000);
  return `test-${timestamp}-${random}@example.com`;
}

export async function getOneTimeCodeFromMailSlurper(email: string) : Promise<string> {
  const emailsResponse = await fetch("http://localhost:8085/mail", { method: "GET" });
  const allEmails = (await emailsResponse.json()).mailItems
  const theEmail = allEmails.find(e => e.toAddresses.includes(email));
  const matches = theEmail.subject.match(/passcode (\d{6})/)
  console.log(matches);
  return matches?.[1] ?? "";
}

export function defaultPassword() : string {
  return "password123";
}