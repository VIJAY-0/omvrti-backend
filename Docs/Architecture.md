# 🏗️ Booking System — Full Architecture Design
## Autopilot + Copilot Hybrid with Recommendation Engine, Budget, Rewards & Replanning

---

## 📑 Table of Contents

1. [Top-Level System Architecture](#1-top-level-system-architecture)
2. [Meeting Planner Flow](#2-meeting-planner-flow)
3. [Booking Flow — End to End](#3-booking-flow--end-to-end)
4. [Recommendation Engine Architecture](#4-recommendation-engine-architecture)
5. [Autopilot Decision Engine (Budget + Preferences)](#5-autopilot-decision-engine-budget--preferences)
6. [Rewards & Redemption Flow](#6-rewards--redemption-flow)
7. [Copilot Override & Autopilot Recalculation Loop](#7-copilot-override--autopilot-recalculation-loop)
8. [Data Model Overview](#8-data-model-overview)
9. [Component Interaction Summary](#9-component-interaction-summary)

---

## 1. Top-Level System Architecture

> High-level view of all major subsystems and how they connect.

```mermaid
graph TB
    subgraph USER_LAYER["👤 User Layer"]
        U1[Web / Mobile App]
    end

    subgraph ORCHESTRATION["🧭 Orchestration Layer"]
        TO[Trip Orchestrator]
        MP[Meeting Planner Service]
    end

    subgraph INTELLIGENCE["🧠 Intelligence Layer"]
        RE[Recommendation Engine]
        APE[Autopilot Decision Engine]
        CPE[Copilot Interface]
        PE[Policy Engine]
        RW[Rewards Engine]
    end

    subgraph DATA_LAYER["💾 Data Layer"]
        UP[User Profile & Preferences]
        BG[Budget & Policy Store]
        RWS[Rewards Store]
        BK[Booking Store]
        OPT[Options Cache]
    end

    subgraph EXTERNAL["🌐 External Providers"]
        FA[Flight APIs]
        HA[Hotel APIs]
        CA[Cab APIs]
        LYL[Loyalty Programs]
    end

    U1 --> TO
    U1 --> MP
    MP --> TO
    TO --> RE
    TO --> APE
    TO --> CPE
    APE --> PE
    APE --> RW
    RE --> FA
    RE --> HA
    RE --> CA
    RW --> LYL
    RE --> OPT
    APE --> UP
    APE --> BG
    RW --> RWS
    TO --> BK
    UP --> DATA_LAYER
    BG --> DATA_LAYER
```

---

## 2. Meeting Planner Flow

> How a calendar meeting triggers trip planning, resolves attendees, and initiates booking.

```mermaid
flowchart TD
    A([📅 Meeting Created / Updated]) --> B{Source?}

    B -- Calendar Integration --> C[Parse Meeting Details]
    B -- Manual Entry --> D[User Fills Trip Form]

    C --> E[Extract: Date, Location, Attendees]
    D --> E

    E --> F[Resolve Traveler Profiles]
    F --> G{Multiple Travelers?}

    G -- Yes --> H[Group Trip Mode\nShared Itinerary]
    G -- No --> I[Solo Trip Mode]

    H --> J[Aggregate Preferences\n& Policies per Traveler]
    I --> J

    J --> K[Validate Against\nPolicy Engine]
    K --> L{Policy OK?}

    L -- No --> M[Flag Violations\nRequest Approval]
    L -- Yes --> N[Check Rewards Balance\nper Traveler]
    M --> N

    N --> O[Initialize Trip Orchestrator]
    O --> P[Trigger Recommendation Engine]

    P --> Q[/RecommendationSet Ready/]
    Q --> R[Present Autopilot Suggestions]
    R --> S([Booking Flow Begins])

    style A fill:#4CAF50,color:#fff
    style S fill:#2196F3,color:#fff
    style M fill:#FF9800,color:#fff
```

---

## 3. Booking Flow — End to End

> Step-by-step booking with Autopilot default and per-step Copilot override capability.

```mermaid
sequenceDiagram
    actor U as 👤 User
    participant TO as Trip Orchestrator
    participant RE as Recommendation Engine
    participant APE as Autopilot Engine
    participant RW as Rewards Engine
    participant PE as Policy Engine
    participant BL as Booking Layer

    U->>TO: Start Trip (Meeting / Manual)
    TO->>RE: Fetch Options (Flight, Hotel, Cab)
    RE-->>TO: RecommendationSet [F1..F3, H1..H3, C1..C3]

    TO->>PE: Validate options against policy
    PE-->>TO: Filtered + Flagged options

    TO->>RW: Check redeemable rewards
    RW-->>TO: Applicable rewards per category

    TO->>APE: Select best per category\n(budget + prefs + rewards)
    APE-->>TO: BestFlight=F1, BestHotel=H1, BestCab=C1

    TO-->>U: Show Autopilot Summary\n(with rewards applied)

    Note over U,TO: ── FLIGHT STEP ──
    U->>TO: Review Flight Step
    alt Accept Autopilot
        U->>TO: Confirm F1
    else Switch to Copilot
        U->>TO: Select F2 (Copilot)
        TO->>APE: Recalculate remaining steps\n(Hotel, Cab) with updated context
        APE-->>TO: Updated H*, C*
    end

    Note over U,TO: ── HOTEL STEP ──
    U->>TO: Review Hotel Step
    alt Accept Autopilot / Recalculated
        U->>TO: Confirm H1
    else Switch to Copilot
        U->>TO: Select H3
        TO->>APE: Recalculate Cab with updated context
    end

    Note over U,TO: ── CAB STEP ──
    U->>TO: Review Cab Step
    U->>TO: Confirm Cab

    U->>TO: Submit Final Booking
    TO->>BL: Commit [ Flight, Hotel, Cab ] + RewardsApplied
    BL-->>U: ✅ Booking Confirmed
```

---

## 4. Recommendation Engine Architecture

> Internal design of the Recommendation Engine: how it fetches, filters, scores, and returns options.

```mermaid
graph LR
    subgraph INPUT["📥 Inputs"]
        TI[Trip Intent\nDates, Origin, Dest]
        UP[User Profile]
        BG[Budget Envelope]
        RW[Rewards Balance]
    end

    subgraph FETCH["🌐 Fetcher Layer"]
        FF[Flight Fetcher]
        HF[Hotel Fetcher]
        CF[Cab Fetcher]
    end

    subgraph FILTER["🔍 Filter Layer"]
        PF[Policy Filter\nBudget / Airline / Class]
        AF[Availability Filter]
        RF[Rewards Eligibility Filter]
    end

    subgraph SCORE["📊 Scoring Layer"]
        FS[Flight Scorer]
        HS[Hotel Scorer]
        CS[Cab Scorer]
    end

    subgraph OUTPUT["📤 Output"]
        RS[RecommendationSet\nRanked lists per category]
    end

    TI --> FF
    TI --> HF
    TI --> CF
    UP --> FILTER
    BG --> PF
    RW --> RF

    FF --> AF --> PF --> RF --> FS
    HF --> AF --> PF --> RF --> HS
    CF --> AF --> PF --> RF --> CS

    FS --> RS
    HS --> RS
    CS --> RS
```

### Scoring Dimensions

```mermaid
graph TD
    S[Option Score] --> P[Price Score\n40% weight]
    S --> T[Duration / Convenience\n25% weight]
    S --> PR[Preference Match\n20% weight]
    S --> RW[Rewards Value\n15% weight]

    P --> P1[Normalized vs budget envelope]
    T --> T1[Travel time + layovers]
    PR --> PR1[Preferred airline / hotel chain\nRoom type / seat class]
    RW --> RW1[Points earned or redeemed]
```

---

## 5. Autopilot Decision Engine (Budget + Preferences)

> How Autopilot selects the single best option per category using budget rules and user preferences.

```mermaid
flowchart TD
    A([Autopilot Triggered]) --> B[Load User Profile]
    B --> C[Load Budget Envelope\nCompany + Personal]
    C --> D[Load Rewards Balance]
    D --> E[Receive Ranked RecommendationSet]

    E --> F{Apply Policy Rules}
    F -- Violates Policy --> G[Remove Option\nFlag for Approval]
    F -- Passes --> H[Budget Check]

    H --> I{Within Budget?}
    I -- Exceeds --> J[Check Rewards\nCan offset cost?]
    J -- Yes --> K[Apply Rewards Discount\nRe-evaluate budget]
    J -- No --> L[Remove from\nAutopilot candidates]
    I -- Within --> M[Apply Preference Score]
    K --> M

    M --> N[Rank candidates by\nWeighted Score]
    N --> O[Select Top Option\nper Category]
    O --> P{Override from\nCopilot on prior step?}

    P -- Yes --> Q[Recalculate using\nUpdated Trip Context]
    Q --> R[Adjusted Best Option]
    P -- No --> R

    R --> S([Return BestOption\nto Orchestrator])

    style A fill:#7B1FA2,color:#fff
    style S fill:#1976D2,color:#fff
    style G fill:#F44336,color:#fff
    style K fill:#4CAF50,color:#fff
```

### Budget Envelope Model

```mermaid
classDiagram
    class BudgetEnvelope {
        +String tripId
        +float totalBudget
        +float flightBudget
        +float hotelBudget
        +float cabBudget
        +String currency
        +BudgetPolicy policy
        +float remainingAfterRewards()
        +bool isWithinBudget(float amount)
    }

    class BudgetPolicy {
        +String[] allowedAirlines
        +String[] allowedCabins
        +String[] preferredHotelChains
        +int maxHotelStars
        +bool requireLowestFare
        +bool allowBusinessClass
        +String approvalRequired
    }

    class UserPreferences {
        +String preferredAirline
        +String preferredSeat
        +String preferredHotelChain
        +String roomType
        +bool windowSeat
        +bool earlyMorningFlight
        +String mealPreference
        +String[] loyaltyPrograms
    }

    BudgetEnvelope --> BudgetPolicy
    BudgetEnvelope --> UserPreferences
```

---

## 6. Rewards & Redemption Flow

> How rewards are checked, applied, and confirmed across the booking lifecycle.

```mermaid
flowchart TD
    A([Trip Initialized]) --> B[Fetch Rewards Profile]
    B --> C[Query Linked Loyalty Accounts\nAirline Miles, Hotel Points, etc.]
    C --> D[Aggregate Redeemable Value\nper Category]

    D --> E{Rewards Available?}
    E -- No --> F[Proceed without rewards]
    E -- Yes --> G[Calculate Redemption Options]

    G --> H[Option A: Full Redemption\nIf points cover full cost]
    G --> I[Option B: Partial Redemption\nOffset cost to fit budget]
    G --> J[Option C: Earn Mode\nNo redemption, maximize earning]

    H --> K[Mark as Rewards-Funded\nFlag for provider]
    I --> L[Apply Discount\nUpdate remaining cost]
    J --> M[Show Estimated Points Earned]

    K --> N[Add to Final Booking Payload]
    L --> N
    M --> N

    N --> O{User Confirms\nor Copilot Changes?}
    O -- Confirms --> P[Lock Rewards Redemption]
    O -- Copilot Change --> Q[Re-evaluate Rewards\nfor New Selection]
    Q --> G

    P --> R([Rewards Applied to Booking])

    style A fill:#FF6F00,color:#fff
    style R fill:#2E7D32,color:#fff
    style Q fill:#7B1FA2,color:#fff
```

### Rewards Data Model

```mermaid
classDiagram
    class RewardsProfile {
        +String userId
        +List~LoyaltyAccount~ accounts
        +float totalEstimatedValue()
        +List~RedemptionOption~ getOptions(String category)
    }

    class LoyaltyAccount {
        +String programId
        +String programName
        +String category
        +int pointsBalance
        +float pointValueUSD
        +Date expiryDate
        +bool isActive
    }

    class RedemptionOption {
        +String optionId
        +String type
        +int pointsRequired
        +float discountValue
        +String applicableTo
        +bool isFullyFunded
    }

    class RewardsTransaction {
        +String bookingId
        +String accountId
        +int pointsRedeemed
        +int pointsEarned
        +float valueApplied
        +String status
    }

    RewardsProfile "1" --> "*" LoyaltyAccount
    RewardsProfile --> RedemptionOption
    RedemptionOption --> RewardsTransaction
```

---

## 7. Copilot Override & Autopilot Recalculation Loop

> The core interaction loop — when user overrides, how the system responds and updates downstream steps.

```mermaid
stateDiagram-v2
    [*] --> AutopilotReady : Trip initialized

    AutopilotReady --> ReviewFlight : Show Flight Step
    ReviewFlight --> FlightConfirmed : User Accepts
    ReviewFlight --> FlightOverridden : User Picks Copilot

    FlightOverridden --> RecalcHotelCab : Recalculate Hotel & Cab\nbased on new flight context
    RecalcHotelCab --> ReviewHotel : Updated hotel suggestions

    FlightConfirmed --> ReviewHotel : Show Hotel Step

    ReviewHotel --> HotelConfirmed : User Accepts
    ReviewHotel --> HotelOverridden : User Picks Copilot

    HotelOverridden --> RecalcCab : Recalculate Cab\nbased on hotel context
    RecalcCab --> ReviewCab : Updated cab suggestions

    HotelConfirmed --> ReviewCab : Show Cab Step

    ReviewCab --> CabConfirmed : User Accepts
    ReviewCab --> CabOverridden : User Picks Copilot

    CabOverridden --> CabConfirmed : Copilot selection locked

    CabConfirmed --> FinalReview : Show Summary
    FinalReview --> BookingSubmitted : User Confirms
    FinalReview --> ReviewFlight : User Goes Back

    BookingSubmitted --> [*]
```

### Recalculation Logic

```mermaid
flowchart LR
    A[Copilot Selection Made\ne.g. User picks F2] --> B[Capture Override Context]
    B --> C{What changed?}

    C -- Arrival Time Changed --> D[Re-score Hotels\nby proximity to arrival window]
    C -- Budget Consumed Changed --> E[Adjust remaining budget\nfor Hotel + Cab]
    C -- Location Changed --> F[Update Cab origin point]
    C -- Multiple factors --> G[Combined recalculation]

    D --> H[Re-rank Options]
    E --> H
    F --> H
    G --> H

    H --> I[New Autopilot Best\nfor remaining steps]
    I --> J[Present updated recommendations\nmarked as Recalculated]
```

---

## 8. Data Model Overview

> Core entities and relationships across the full system.

```mermaid
erDiagram
    TRIP {
        string tripId PK
        string userId
        string meetingId
        string status
        datetime createdAt
    }

    RECOMMENDATION_SET {
        string setId PK
        string tripId FK
        json flights
        json hotels
        json cabs
        datetime generatedAt
    }

    BOOKING {
        string bookingId PK
        string tripId FK
        string flightId
        string hotelId
        string cabId
        string flightMode
        string hotelMode
        string cabMode
        float totalCost
        float rewardsApplied
        string status
    }

    USER_PROFILE {
        string userId PK
        json preferences
        json loyaltyAccounts
        string companyId
    }

    BUDGET_ENVELOPE {
        string envelopeId PK
        string tripId FK
        float totalBudget
        float flightBudget
        float hotelBudget
        float cabBudget
        json policyRules
    }

    REWARDS_PROFILE {
        string rewardsId PK
        string userId FK
        json accounts
        float totalAvailableValue
    }

    REWARDS_TRANSACTION {
        string txId PK
        string bookingId FK
        string programId
        int pointsRedeemed
        float valueApplied
    }

    TRIP ||--|| RECOMMENDATION_SET : "generates"
    TRIP ||--|| BOOKING : "results in"
    TRIP ||--|| BUDGET_ENVELOPE : "has"
    USER_PROFILE ||--|| REWARDS_PROFILE : "has"
    BOOKING ||--o{ REWARDS_TRANSACTION : "uses"
    USER_PROFILE ||--o{ TRIP : "creates"
```

---

## 9. Component Interaction Summary

> Full system sequence showing all components talking to each other for a complete booking.

```mermaid
sequenceDiagram
    actor U as 👤 User
    participant MP as Meeting Planner
    participant TO as Trip Orchestrator
    participant RE as Recommendation Engine
    participant PE as Policy Engine
    participant APE as Autopilot Engine
    participant RW as Rewards Engine
    participant CPE as Copilot Interface
    participant BL as Booking Layer
    participant DB as Database

    U->>MP: Create / link meeting
    MP->>TO: Initialize trip with context
    TO->>DB: Load UserProfile + BudgetEnvelope
    DB-->>TO: Profile + Budget

    TO->>RE: Fetch options (flight/hotel/cab)
    RE-->>TO: Raw RecommendationSet

    TO->>PE: Validate against policy
    PE-->>TO: Filtered options

    TO->>RW: Get rewards profile
    RW-->>TO: Available redemptions

    TO->>APE: Run autopilot\n(options + budget + prefs + rewards)
    APE-->>TO: BestFlight, BestHotel, BestCab

    TO-->>U: Present Autopilot Summary

    loop Per Booking Step
        U->>CPE: Review step
        alt Accepts Autopilot
            CPE-->>TO: Confirm autopilot selection
        else Switches to Copilot
            CPE->>TO: Override with user selection
            TO->>APE: Recalculate remaining steps
            APE-->>TO: Adjusted recommendations
            TO-->>U: Show updated steps
        end
    end

    U->>TO: Submit final booking
    TO->>RW: Lock rewards redemption
    RW-->>TO: Redemption confirmed
    TO->>BL: Commit booking
    BL->>DB: Persist booking + rewards tx
    DB-->>BL: Saved
    BL-->>U: ✅ Booking Confirmed + Summary
```

---

## 🧩 Design Decisions & Brainstorm Notes

### Recommendation Engine — Key Design Choices

| Concern | Decision | Rationale |
|---|---|---|
| Caching | Cache options by (trip_hash) for 15 min | Avoid redundant API calls |
| Staleness | Invalidate if budget/prefs change | Ensure accurate repricing |
| Recalculation scope | Only recalculate downstream steps | Don't invalidate confirmed steps |
| Scoring weights | Configurable per company policy | Enterprise customers differ |
| Rewards integration | Separate engine, feeds into scoring | Clean separation of concerns |

### Autopilot Recalculation Triggers

- ✅ User picks a different flight → recalculate hotel (proximity) + cab (route)
- ✅ User picks a different hotel → recalculate cab (pickup point)
- ✅ Budget consumed changes → re-rank remaining options by affordability
- ❌ User picks same-tier option → no recalculation needed (skip for performance)

### Rewards Modes

| Mode | When to apply | User action needed |
|---|---|---|
| Auto-Redeem | Points cover delta to fit budget | None (Autopilot applies) |
| Suggest Redeem | Points can unlock better option | User confirms |
| Earn Mode | No redemption, rack up points | User selects |
| Mixed | Partial redemption | User adjusts slider |

### Future Enhancements (Roadmap)

- **Cross-step context awareness** — flight arrival time influences hotel check-in suggestions
- **Group booking mode** — split-cost with shared itinerary
- **Real-time repricing** — live price refresh during copilot session
- **AI personalization layer** — learn from past trips to improve autopilot accuracy
- **Carbon footprint score** — add sustainability dimension to scoring

---

*Architecture Version: 1.0 | Last Updated: 2026*