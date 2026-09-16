# Yarnstash — User Stories

Backlog for the yarn stash & project tracker. Grouped by epic, ready to paste into Jira.

**Legend** — Priority: `Must` = MVP, `Should` = if time allows, `Could` = stretch.
`Day` = target day in the 14-day build plan.
`Status`: ✅ done / 🟡 partial / 🚧 in progress / ⛔ descoped / — not started.

---

## Progress — as of 2026-09-16 (day 4)

| Epic | Done | Partial | Remaining |
|---|---|---|---|
| 1 — Stash Management | 4 | 2 | 3 |
| 2 — Project Tracking | 3 | 1 | 2 |
| 3 — Yarn Allocation | 0 | 0 | 6 |
| 4 — Insights | 1 | 0 | 3 |
| 5 — Non-Functional | 1 | 0 | 3 |
| 6 — Frontend | 0 | 0 | 7 |
| **Total** | **9** | **3** | **18** + 1 descoped |

**Completed since last check:** 2.2 (status, incl. `PLANNED` default), 2.3 (dates, all three criteria), 2.5 (notes).

**Open gaps on "partial" items**
- **1.5** — delete works and 404s correctly, but the "cannot delete allocated yarn → 409" rule needs `ProjectYarn` to exist. Revisit on day 5.
- **1.6** — constraints reject bad input with 400, but the response body does not name the failing field, and an invalid enum value does not list the valid options. Closing most of this gap costs two lines in `application.properties`
  (`server.error.include-message=always`, `server.error.include-binding-errors=always`);
  the rest needs a `@RestControllerAdvice`, deferred until the React forms need field-level errors.
- **2.1** — full CRUD works, but there is no `pattern` field on `Project`. Deliberately deferred: how a pattern is best represented (free text, URL, or a separate entity with source/designer) is still an open design question rather than an oversight.

**Known behaviour, by design**
- `PUT` is a full replacement. Omitting an optional field (`status`, `notes`, `hookSize`, `startedOn`, `finishedOn`) resets it to its default rather than leaving it untouched. Clients must send the complete resource. Partial updates would require `PATCH`, which is out of scope.

---

## Epic 1 — Stash Management

### 1.1 Add a yarn to the stash
Record a new yarn with brand, colorway, fiber content, weight category, number of skeins, and yards per skein. The API assigns an id and returns the created yarn including its computed total yardage.

- Priority: Must
- Day: 1
- Status: ✅ done

### 1.2 List the whole stash
Return every yarn in the stash as a single collection, each entry including its computed total yardage.

- Priority: Must
- Day: 1
- Status: ✅ done

### 1.3 View a single yarn's details
Fetch one yarn by id and return its full details. Requesting an id that does not exist returns 404.

- Priority: Must
- Day: 1
- Status: ✅ done

### 1.4 Edit an existing yarn
Update any field on a yarn — most often the skein count, after some has been used. Updating an id that does not exist returns 404.

**Acceptance criteria**
- `PUT /api/yarns/{id}` with a valid body returns 200 and the updated yarn
- Total yardage is recalculated from the new values
- Unknown id returns 404
- Invalid body returns 400 (see 1.6)

- Priority: Must
- Day: 3
- Status: ✅ done

### 1.5 Delete a yarn
Remove a yarn from the stash so it reflects what has been given away or used up.

**Acceptance criteria**
- `DELETE /api/yarns/{id}` returns 204 on success
- Unknown id returns 404
- A yarn currently allocated to a project cannot be deleted — returns 409 with an explanatory message

- Priority: Must
- Day: 3
- Status: 🟡 partial — delete and 404 work; the 409-when-allocated rule needs `ProjectYarn` (day 5)

### 1.6 Clear validation errors on invalid input
Reject malformed or nonsensical input with a structured error response naming the offending fields, so the client can show a useful message. No stack trace or internal detail is ever returned.

**Acceptance criteria**
- Negative `skeins` or `yardsPerSkein` returns 400 identifying the field and reason
- Missing `brand` returns 400, not 500
- An unrecognised `weight` value returns 400 listing the valid options
- All error responses share one consistent JSON shape (timestamp, status, message, field errors)

- Priority: Must
- Day: 3
- Status: 🟡 partial — constraints reject bad input with 400; response body does not yet name the failing field, and invalid enum values do not list valid options

### 1.7 Filter the stash by weight and fiber
Narrow the stash list by weight category and/or fiber content to find candidates for a specific pattern. Filters combine.

**Acceptance criteria**
- `GET /api/yarns?weight=WORSTED` returns only worsted yarns
- Fiber matching is partial and case-insensitive (`wool` matches `100% wool` and `65% wool, 35% alpaca`)
- Filters can be combined
- No filters returns the full stash

- Priority: Must
- Day: 5
- Status: —

### 1.8 Sort the stash
Order the stash list by a chosen field — total yardage or date acquired — ascending or descending.

- Priority: Should
- Day: 5
- Status: —

### 1.9 Paginate the stash list
Return the stash in pages with total count and page metadata, so the list stays fast as the stash grows.

**Acceptance criteria**
- `page` and `size` query parameters are supported
- Response includes total elements and total pages
- A sensible default page size applies when none is given

- Priority: Should
- Day: 5
- Status: —

---

## Epic 2 — Project Tracking

### 2.1 Create a project
Record a new project with a name, pattern name or source, and hook/needle size.

- Priority: Must
- Day: 4
- Status: 🟡 partial — full CRUD at `/api/projects`; `pattern` field not yet added

### 2.2 Track project status
Move a project through `PLANNED`, `IN_PROGRESS`, `FINISHED`, and `FROGGED` so it is clear what is actually on the needles.

**Acceptance criteria**
- Status is one of the four defined values; anything else returns 400
- New projects default to `PLANNED`
- Status can be changed on an existing project

- Priority: Must
- Day: 4
- Status: ✅ done — enum stored as text, invalid values rejected with 400, status changes on update, and an omitted status defaults to `PLANNED` in `applyStatusRules()`

### 2.3 Record start and finish dates
Store when a project was started and finished, to see how long things take.

**Acceptance criteria**
- Finish date cannot precede start date — returns 400
- Both dates are optional (a planned project has neither)
- Setting status to `FINISHED` without a finish date defaults it to today

- Priority: Must
- Day: 4
- Status: ✅ done — `@PastOrPresent` on both dates, `@AssertTrue` cross-field order check, and `applyStatusRules()` defaults the finish date. Moving away from `FINISHED` clears it again.

### 2.4 Filter projects by status
List only projects in a given status, so works-in-progress can be viewed on their own.

- Priority: Should
- Day: 5
- Status: —

### 2.5 Keep notes on a project
Store free-text notes against a project to record pattern modifications and lessons learned.

- Priority: Should
- Day: 4
- Status: ✅ done — `TEXT` column, capped at 5000 chars on input

### 2.6 Frog a project and reclaim its yarn
Setting a project to `FROGGED` releases all yarn allocated to it back into available stash, keeping stash numbers honest.

**Acceptance criteria**
- Frogging a project returns its allocated yardage to the yarns' available totals
- The project and its allocation history are retained, not deleted
- Un-frogging is not supported — a re-attempt is a new project

- Priority: Could
- Day: 5
- Status: —

---

## Epic 3 — Yarn Allocation ⭐

Core domain of the application. These stories carry the interesting business rules.

### 3.1 Allocate yarn to a project
Attach a yarn to a project and record how many yards of it were used, so it is clear what went into each project.

**Acceptance criteria**
- `POST /api/projects/{id}/yarns` accepts a yarn id and a yard amount
- Allocated amount must be greater than zero
- Unknown project id or yarn id returns 404
- The same yarn cannot be allocated to the same project twice — amend the existing allocation instead

- Priority: Must
- Day: 4
- Status: —

### 3.2 Allocate multiple yarns to one project
Support several different yarns on a single project, to track colourwork and stripes.

- Priority: Must
- Day: 4
- Status: —

### 3.3 Show available yardage per yarn
Expose each yarn's available yardage — total owned minus everything committed to projects — so the same skein is not planned into two projects.

**Acceptance criteria**
- Yarn responses include `totalYards`, `allocatedYards`, and `availableYards`
- `availableYards` equals `totalYards` minus the sum of active allocations
- Yarn with no allocations reports `availableYards` equal to `totalYards`

- Priority: Must
- Day: 5
- Status: —

### 3.4 Prevent over-allocation
Reject any allocation that would exceed a yarn's available yardage, so stash totals can never go negative.

**Acceptance criteria**
- Allocating 400 yards of a yarn with 328 available returns 409, stating requested vs available
- Allocating exactly the available amount succeeds and leaves 0 available
- Two sequential allocations of 200 against 328 available: the first succeeds, the second is rejected
- Deleting or frogging a project releases its yardage back to available

- Priority: Must
- Day: 5
- Status: —

### 3.5 See which projects used a given yarn
From a yarn, list the projects it has been allocated to and how much went to each — useful for tracing a discontinued colourway.

- Priority: Should
- Day: 5
- Status: —

### 3.6 Remove or amend an allocation
Detach a yarn from a project, or change the recorded yardage, to correct a mistake. Available yardage updates accordingly.

**Acceptance criteria**
- `DELETE /api/projects/{projectId}/yarns/{yarnId}` returns 204 and restores available yardage
- Amending an allocation upward is subject to the same availability check as 3.4

- Priority: Should
- Day: 5
- Status: —

---

## Epic 4 — Insights

### 4.1 Stash totals
Report the number of distinct yarns and the total yardage held across the whole stash.

- Priority: Must
- Day: 1
- Status: ✅ done

### 4.2 Yardage breakdown by weight
Report total yardage grouped by weight category, to reveal what the stash is actually made of.

- Priority: Should
- Day: 5
- Status: —

### 4.3 Projects finished per period
Count projects completed within a given year or month.

- Priority: Could
- Day: 5
- Status: —

### 4.4 Longest-unused yarns
Surface yarns with the oldest acquisition date and no allocations, to prioritise using them up.

- Priority: Could
- Day: stretch
- Status: —

---

## Epic 5 — Non-Functional

### 5.1 Data persists across restarts
Stash and project data is stored in PostgreSQL and survives an application restart.

- Priority: Must
- Day: 2
- Status: ✅ done — PostgreSQL 18 in a container, named volume, JPA persistence

### 5.2 Versioned, repeatable schema
All schema changes are defined as Flyway migrations under version control. No reliance on Hibernate auto-DDL.

- Priority: Must
- Day: 2
- Status: ⛔ descoped — using `spring.jpa.hibernate.ddl-auto=update` instead. Deliberate tradeoff for a short exploratory build with a single schema consumer; a production project would use versioned migrations. Recorded in the README tradeoffs section.

### 5.3 Automated test coverage of core logic
Controller endpoints are covered by MockMvc tests and the allocation rules by unit tests. `./mvnw test` passes.

- Priority: Must
- Day: 6
- Status: —

### 5.4 Full stack runs with one command
`docker compose up` starts the database, backend, and frontend together with no manual setup.

- Priority: Must
- Day: 12
- Status: —

### 5.5 CI on every push
GitHub Actions builds the project and runs the test suite on each push to the default branch.

- Priority: Should
- Day: 13
- Status: —

---

## Epic 6 — Frontend

### 6.1 View the stash in the browser
Render the stash as a list or card grid fetched from the API, showing brand, colorway, weight, and available yardage.

- Priority: Must
- Day: 8
- Status: —

### 6.2 Add and edit yarn from the UI
Provide forms to create and update yarn, surfacing server-side validation errors against the relevant fields.

- Priority: Must
- Day: 9
- Status: —

### 6.3 Delete yarn from the UI
Delete a yarn with a confirmation step, and show a clear message when deletion is blocked by an existing allocation.

- Priority: Must
- Day: 9
- Status: —

### 6.4 Manage projects in the UI
List projects, create and edit them, and change status.

- Priority: Must
- Day: 10
- Status: —

### 6.5 Allocate yarn to a project in the UI
Pick a yarn from the stash and enter a yardage to allocate, with available yardage shown live and over-allocation errors surfaced inline.

- Priority: Must
- Day: 10
- Status: —

### 6.6 Filter and sort the stash in the UI
Expose the weight and fiber filters and the sort options as UI controls.

- Priority: Should
- Day: 11
- Status: —

### 6.7 Loading, error, and empty states
Every view handles in-flight requests, failed requests, and empty result sets without looking broken.

- Priority: Must
- Day: 11
- Status: —

---

## Out of Scope

Deliberately excluded to keep the two-week build achievable. Recorded here so the omission reads as a decision rather than an oversight.

| Item | Reason |
|---|---|
| Authentication / multi-user | Multi-day effort; single-user is a valid design choice for a personal tracker |
| Photo uploads | File storage and serving is a subsystem of its own — store an image URL instead |
| Gauge / swatch calculator | Arithmetic only; adds no new learning about the stack |
| Ravelry API import | Requires third-party OAuth |
| Pattern PDF storage | Same cost as photo uploads, with worse payoff |
| Cloud deployment | `docker compose up` is sufficient to demonstrate the project |
| Tags, wishlist, dark mode | Polish; revisit once the MVP ships |
