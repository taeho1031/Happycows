import { fireEvent, render, screen } from "@testing-library/react";
import { BrowserRouter as Router } from "react-router-dom";
import UpdateCowHealthForm from "main/components/Jobs/UpdateCowHealthJobForm";
import jobsFixtures from "fixtures/jobsFixtures";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedNavigate
}));

describe("UpdateCowHealthForm tests", () => {
  it("renders correctly with the right defaults", async () => {
    render(
      <Router >
        <UpdateCowHealthForm jobs={jobsFixtures.sixJobs}/>
      </Router>
    );

    expect(screen.getByText(/Submit/)).toBeInTheDocument();
  });

});
