# Recommendation Engine

```mermaid
flowchart TD
  IN([Input<br> · TripContext <br>· remainingBudget <br>· Categories to score]) --> FETCH

  subgraph FETCH [1 — Fetch options in parallel]
    direction LR
    FP[FlightProvider]
    HP[HotelProvider]
    CP[CabProvider]
  end

  FETCH --> LOAD
  LOAD[2 — Load user context<br> · UserPreferenceProfile <br>· PolicyProfile <br>· RewardAccount] --> SCORE

  subgraph SCORE [3 — Score each option]
    direction TB
    PS[<b>PreferenceScorer</b><br>Score option vs <br> UserPreferenceProfile <br> per-dimension  weighted sum <br><br>→ PreferenceScore]
    PLS[<b>PolicyScorer</b><br>Check option vs PolicyProfile <br>+ <br>·remainingBudget<br>·compute coverage <br>· overage <br>· rewardGrant <br>→ PolicyScore]
    CS[<b>CompositeScorer</b><br>compositeScore = prefWeight × prefScore + policyWeight × polScore<br>set autopilotEligible <br> if within policy <br>→ FinalScore]
    PS --> CS
    PLS --> CS
  end

  SCORE --> RANK

  subgraph RANK [4 — Rank and split]
    direction TB
    R1[Sort all options by compositeScore descending]
    R2{autopilotEligible?}
    R3[Autopilot pick — top-ranked in-policy option]
    R4[Copilot list — all options sorted by compositeScore<br>out-of-policy options tagged with redeemRequired · walletGap]
    R1 --> R2
    R2 -->|yes — top one| R3
    R2 -->|all options| R4
  end

  RANK --> OUT([Output<br>ScoredOptionSet per category<br>autopilotPick <br>· copilotList <br>· rewardPointsGranted])
```
# 🚀 Recommendation Engine — System Design & Flow

## 🧭 Overview

The **Recommendation Engine** evaluates and ranks travel options (Flights, Hotels, Cabs) using:

* User preferences
* Corporate policy constraints
* Available budget
* Reward optimization


## ⚙️ Step 1 — Fetch Options (Parallelized)

* Providers queried in parallel
* Results normalized into common structure
* Add timeouts and fallback providers

---

## 👤 Step 2 — Load User Context

```
UserPreferenceProfile
PolicyProfile
RewardAccount
```
---

## 🧮 Step 3 — Scoring Engine

### Preference Score

```
PreferenceScore = Σ (w_i × s_i)
```

### Policy Score

```
PolicyScore = f(compliance, coverage, overage, rewards)
```

### Composite Score

```
FinalScore = w_p × PreferenceScore + w_c × PolicyScore
```

### Eligibility

```
autopilotEligible = within policy constraints
```

---

## 📊 Step 4 — Ranking & Decisioning

### Ranking

```
Sort all options by FinalScore (descending)
```

### Autopilot

```
Pick top option where autopilotEligible = true
```

### Copilot

```
All options sorted by FinalScore
```

Tags:

* redeemRequired
* walletGap

---

## 📤 Output Contract

```json
{
  "category": "Flights | Hotels | Cabs",
  "autopilotPick": {},
  "copilotList": [],
  "rewardPointsGranted": 0
}
```
---

## 💡 Design Suggestions

### Pluggable Scorers

```
score(option, context) → number
```

### Dynamic Weights

* Adjust based on user type
* Adjust based on trip type

### Caching

* Provider results (short TTL)
* User profile (medium TTL)
* Scored results (session)

### Observability

* Track score breakdown
* Monitor autopilot vs copilot

### Failure Handling

* Partial results on provider failure
* Skip failed scorers

---

## 🎯 Summary

The system separates:

* Fetching
* Context loading
* Scoring
* Decisioning

This makes it scalable, extensible, and explainable.
