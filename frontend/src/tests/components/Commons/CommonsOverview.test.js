import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

import CommonsOverview from "main/components/Commons/CommonsOverview"; 
import PlayPage from "main/pages/PlayPage";
//import commonsFixtures from "fixtures/commonsFixtures";
import commonsPlusFixtures from "fixtures/commonsPlusFixtures";
import leaderboardFixtures from "fixtures/leaderboardFixtures";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useParams: () => ({
        commonsId: 1
    }),
    useNavigate: () => mockNavigate
}));

describe("CommonsOverview tests", () => {

    const queryClient = new QueryClient();
    const axiosMock = new AxiosMockAdapter(axios);

    beforeEach(() => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    });

    test("renders without crashing", () => {
        render(
            //<CommonsOverview commons={commonsFixtures.oneCommons[0]} />
            <CommonsOverview commonsPlus={commonsPlusFixtures.oneCommonsPlus[0]} />
        );
    });

    test("Redirects to the LeaderboardPage for an admin when you click visit", async () => {
        //apiCurrentUserFixtures.adminUser.user.commons = commonsFixtures.oneCommons[0];
        apiCurrentUserFixtures.adminUser.user.commonsPlus = commonsPlusFixtures.oneCommonsPlus;
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.adminUser);
        //axiosMock.onGet("/api/commons", {params: {id:1}}).reply(200, commonsFixtures.oneCommons);
        axiosMock.onGet("/api/commons/plus", {params: {id:4}}).reply(200, commonsPlusFixtures.oneCommonsPlus);
        axiosMock.onGet("/api/leaderboard/all").reply(200, leaderboardFixtures.threeUserCommonsLB);
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <PlayPage />
                </MemoryRouter>
            </QueryClientProvider>
        );
        await waitFor(() => {
            expect(axiosMock.history.get.length).toEqual(6);
        });
        expect(await screen.findByTestId("user-leaderboard-button")).toBeInTheDocument();
        const leaderboardButton = screen.getByTestId("user-leaderboard-button");
        fireEvent.click(leaderboardButton);
        //expect(mockNavigate).toBeCalledWith({ "to": "/leaderboard/1" });
    });

    test("No LeaderboardPage for an ordinary user when commons has showLeaderboard = false", async () => {
        /*const ourCommons = {
            ...commonsFixtures.oneCommons,
            showLeaderboard : false
        };*/
        const ourCommons = {
                    ...commonsPlusFixtures.oneCommonsPlus.commons,
                    showLeaderboard : false
        };
        //apiCurrentUserFixtures.userOnly.user.commons = commonsFixtures.oneCommons[0];
        apiCurrentUserFixtures.userOnly.user.commonsPlus = commonsPlusFixtures.oneCommonsPlus[0];
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        //axiosMock.onGet("/api/commons", {params: {id:1}}).reply(200, commonsFixtures.oneCommons);
        axiosMock.onGet("/api/commons/plus", {params: {id:4}}).reply(200, commonsPlusFixtures.oneCommonsPlus);
        axiosMock.onGet("/api/leaderboard/all").reply(200, leaderboardFixtures.threeUserCommonsLB);
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <PlayPage />
                </MemoryRouter>
            </QueryClientProvider>
        );
        await waitFor(() => {
            expect(axiosMock.history.get.length).toEqual(2);
        });
        expect(() => screen.getByTestId("user-leaderboard-button")).toThrow();
    });
});