// ------------------------------------------------------------
// Interest Calculator: the page's behaviour
//   1. Shows only the boxes that are needed for what you want to find
//   2. Shows the steps (equation, then the equation with your numbers)
//   3. Sends the numbers to the Java app and shows the answer it sends back
// ------------------------------------------------------------

const TIMES = '\u00d7';
const DIV = '\u00f7';
const MINUS = '\u2212';

// What the user has chosen
const state = {
    type: 'simple',      // 'simple' or 'compound'
    find: 'AMOUNT',      // AMOUNT, PRINCIPAL, RATE or TIME
    known: 'INTEREST'    // simple interest only: INTEREST or AMOUNT
};

// Every time something changes or a new calculation starts, this number goes up.
// A calculation that is still waiting checks it, and stops if it is out of date.
let runId = 0;

// How many parts a year is divided into for each time unit (years need no division).
// This is only for DISPLAYING the step; the Java app does the real conversion.
const DIVISORS = {1: null, 2: 12, 3: 52, 4: 360, 5: 365, 6: 366};

// ------------------------------------------------------------
// The equations. {P} {r} {t} {I} {S} {m} are placeholders for the symbols.
// "targets" are the symbols we are finding, so they always stay as symbols.
// ------------------------------------------------------------
const EQUATIONS = {
    simple: {
        AMOUNT: {
            targets: ['I', 'S'],
            lines: [
                `{I} = {P} ${TIMES} ({r} ${DIV} 100) ${TIMES} {t}`,
                `{S} = {P} + {I}`
            ]
        },
        PRINCIPAL: {
            INTEREST: {
                targets: ['P'],
                lines: [`{P} = {I} ${DIV} (({r} ${DIV} 100) ${TIMES} {t})`]
            },
            AMOUNT: {
                targets: ['P'],
                lines: [`{P} = {S} ${DIV} (1 + ({r} ${DIV} 100) ${TIMES} {t})`]
            }
        },
        RATE: {
            INTEREST: {
                targets: ['r'],
                lines: [`{r} = {I} ${DIV} ({P} ${TIMES} {t}) ${TIMES} 100`]
            },
            AMOUNT: {
                targets: ['r'],
                lines: [`{r} = ({S} ${MINUS} {P}) ${DIV} ({P} ${TIMES} {t}) ${TIMES} 100`]
            }
        },
        TIME: {
            INTEREST: {
                targets: ['t'],
                lines: [`{t} = {I} ${DIV} ({P} ${TIMES} ({r} ${DIV} 100))`]
            },
            AMOUNT: {
                targets: ['t'],
                lines: [`{t} = ({S} ${MINUS} {P}) ${DIV} ({P} ${TIMES} ({r} ${DIV} 100))`]
            }
        }
    },
    compound: {
        AMOUNT: {
            targets: ['S', 'I'],
            lines: [
                `{S} = {P} ${TIMES} (1 + {r} ${DIV} (100 ${TIMES} {m}))<sup>{m} ${TIMES} {t}</sup>`,
                `{I} = {S} ${MINUS} {P}`
            ]
        },
        PRINCIPAL: {
            targets: ['P'],
            lines: [`{P} = {S} ${DIV} (1 + {r} ${DIV} (100 ${TIMES} {m}))<sup>{m} ${TIMES} {t}</sup>`]
        },
        RATE: {
            targets: ['r'],
            lines: [`{r} = {m} ${TIMES} (({S} ${DIV} {P})<sup>1 ${DIV} ({m} ${TIMES} {t})</sup> ${MINUS} 1) ${TIMES} 100`]
        },
        TIME: {
            targets: ['t'],
            lines: [`{t} = ln({S} ${DIV} {P}) ${DIV} ({m} ${TIMES} ln(1 + {r} ${DIV} (100 ${TIMES} {m})))`]
        }
    }
};

function getEquation() {
    const group = EQUATIONS[state.type][state.find];
    if (state.type === 'simple' && state.find !== 'AMOUNT') {
        return group[state.known];
    }
    return group;
}

// ------------------------------------------------------------
// Small helpers
// ------------------------------------------------------------
function $(id) {
    return document.getElementById(id);
}

function sleep(milliseconds) {
    return new Promise((resolve) => setTimeout(resolve, milliseconds));
}

function fmt(value, minDigits, maxDigits) {
    return Number(value).toLocaleString('en-US', {
        minimumFractionDigits: minDigits,
        maximumFractionDigits: maxDigits
    });
}

// Which boxes are needed for the current choices
function visibleFields() {
    const knownField = state.known === 'INTEREST' ? 'interest' : 'amount';

    if (state.type === 'simple') {
        if (state.find === 'AMOUNT') return ['principal', 'rate', 'time'];
        if (state.find === 'PRINCIPAL') return [knownField, 'rate', 'time'];
        if (state.find === 'RATE') return ['principal', knownField, 'time'];
        return ['principal', 'rate', knownField];
    }

    if (state.find === 'AMOUNT') return ['principal', 'rate', 'time', 'timesPerYear'];
    if (state.find === 'PRINCIPAL') return ['amount', 'rate', 'time', 'timesPerYear'];
    if (state.find === 'RATE') return ['principal', 'amount', 'time', 'timesPerYear'];
    return ['principal', 'amount', 'rate', 'timesPerYear'];
}

// The number typed in a box, or null if the box is empty
function readNumber(id) {
    const text = $(id).value.trim();
    if (text === '') return null;
    return Number(text);
}

// ------------------------------------------------------------
// Updating the page when the user changes a choice
// ------------------------------------------------------------
function updateForm() {
    document.querySelectorAll('.tab').forEach((button) => {
        button.classList.toggle('active', button.dataset.type === state.type);
    });
    document.querySelectorAll('#find-chips .chip').forEach((button) => {
        button.classList.toggle('active', button.dataset.find === state.find);
    });
    document.querySelectorAll('#known-chips .chip').forEach((button) => {
        button.classList.toggle('active', button.dataset.known === state.known);
    });

    $('known-box').hidden = !(state.type === 'simple' && state.find !== 'AMOUNT');

    const visible = visibleFields();
    document.querySelectorAll('.field').forEach((box) => {
        box.hidden = !visible.includes(box.dataset.field);
    });

    clearOutput();
}

function clearOutput() {
    runId++;
    $('error').hidden = true;
    $('error').textContent = '';
    $('output').hidden = true;
    $('step1').hidden = true;
    $('step2').hidden = true;
    $('step3').hidden = true;
    $('result').hidden = true;
    $('result').innerHTML = '';
    $('calculate').disabled = false;
}

// ------------------------------------------------------------
// Steps: the equation in symbols, then the equation with our numbers
// ------------------------------------------------------------

// The numbers we know, as text, keyed by their symbol
function collectValues() {
    const visible = visibleFields();
    const values = {};

    if (visible.includes('principal')) values.P = String(Number($('principal').value));
    if (visible.includes('rate')) values.r = String(Number($('rate').value));
    if (visible.includes('interest')) values.I = String(Number($('interest').value));
    if (visible.includes('amount')) values.S = String(Number($('amount').value));
    if (visible.includes('timesPerYear')) values.m = $('timesPerYear').value;

    if (visible.includes('time')) {
        const time = String(Number($('time').value));
        const divisor = DIVISORS[$('unit').value];
        values.t = divisor ? `(${time} ${DIV} ${divisor})` : time;
    }

    return values;
}

// Replace every {X} in the text. A target (or an unknown) stays a symbol;
// anything we know becomes its number.
function fillTemplate(template, targets, values) {
    return template.replace(/\{(\w+)\}/g, (match, key) => {
        const isTarget = targets.includes(key);
        if (!isTarget && values[key] !== undefined) {
            return `<span class="num">${values[key]}</span>`;
        }
        const cssClass = isTarget ? 'sym target' : 'sym';
        return `<span class="${cssClass}">${key}</span>`;
    });
}

function renderEquations() {
    const equation = getEquation();
    const values = collectValues();

    const symbolLines = equation.lines.map((line) => fillTemplate(line, equation.targets, {}));
    const valueLines = equation.lines.map((line) => fillTemplate(line, equation.targets, values));

    $('eq-symbols').innerHTML = symbolLines.join('<br>');
    $('eq-values').innerHTML = valueLines.join('<br>');
}

// ------------------------------------------------------------
// Talking to the Java app
// ------------------------------------------------------------
function buildRequest() {
    const visible = visibleFields();
    const request = {find: state.find};

    if (state.type === 'simple' && state.find !== 'AMOUNT') {
        request.known = state.known;
    }
    if (visible.includes('principal')) request.principal = readNumber('principal');
    if (visible.includes('rate')) request.rate = readNumber('rate');
    if (visible.includes('interest')) request.interest = readNumber('interest');
    if (visible.includes('amount')) request.amount = readNumber('amount');
    if (visible.includes('time')) {
        request.time = readNumber('time');
        request.unit = Number($('unit').value);
    }
    if (visible.includes('timesPerYear')) {
        request.timesPerYear = Number($('timesPerYear').value);
    }

    return request;
}

// Are any of the boxes we need still empty?
function hasEmptyField() {
    return visibleFields().some((key) => key !== 'timesPerYear' && readNumber(key) === null);
}

async function send(request) {
    const url = state.type === 'simple' ? '/api/simple' : '/api/compound';

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(request)
        });
        const data = await response.json().catch(() => null);

        if (response.ok && data) {
            return {ok: true, data: data};
        }
        if (data && Array.isArray(data.errors)) {
            return {ok: false, errors: data.errors};
        }
        return {ok: false, errors: ['Something went wrong. Please try again.']};
    } catch (error) {
        return {ok: false, errors: ['Could not reach the server. Is the app running?']};
    }
}

// ------------------------------------------------------------
// Showing the answer or the problems
// ------------------------------------------------------------
function showErrors(errors) {
    const list = document.createElement('ul');
    errors.forEach((message) => {
        const item = document.createElement('li');
        item.textContent = message;
        list.appendChild(item);
    });
    $('error').replaceChildren(list);
    $('error').hidden = false;
}

function answerLine(symbol, name, value) {
    return `<div class="line"><span><span class="sym">${symbol}</span> ${name}</span><span>${value}</span></div>`;
}

function showResult(data) {
    const money = (n) => fmt(n, 2, 2);
    const years = data.timeInYears === 1 ? '1 year' : `${fmt(data.timeInYears, 0, 4)} years`;
    let answer = '';

    if (state.find === 'AMOUNT') {
        answer = answerLine('S', 'Amount', money(data.amount)) +
            answerLine('I', 'Interest', money(data.interest));
    } else if (state.find === 'PRINCIPAL') {
        answer = answerLine('P', 'Principal', money(data.principal));
    } else if (state.find === 'RATE') {
        answer = answerLine('r', 'Rate', `${fmt(data.rate, 0, 4)} %`);
    } else {
        answer = answerLine('t', 'Time', years);
    }

    const values =
        `<div><span class="sym">P</span> ${money(data.principal)}</div>` +
        `<div><span class="sym">I</span> ${money(data.interest)}</div>` +
        `<div><span class="sym">S</span> ${money(data.amount)}</div>` +
        `<div><span class="sym">r</span> ${fmt(data.rate, 0, 4)} %</div>` +
        `<div><span class="sym">t</span> ${years}</div>`;

    $('result').innerHTML = `<div class="answer">${answer}</div><div class="values">${values}</div>`;
    $('result').hidden = false;
}

// ------------------------------------------------------------
// The Calculate button
// ------------------------------------------------------------
async function calculate() {
    clearOutput();
    const myRun = runId;
    const request = buildRequest();
    const button = $('calculate');
    button.disabled = true;

    // Something is missing: skip the steps and let the Java app list what is needed
    if (hasEmptyField()) {
        const outcome = await send(request);
        if (myRun !== runId) return;
        button.disabled = false;
        if (outcome.ok) {
            $('output').hidden = false;
            showResult(outcome.data);
        } else {
            showErrors(outcome.errors);
        }
        return;
    }

    // Start the request now, and show the steps while it travels
    const pending = send(request);

    $('output').hidden = false;
    renderEquations();
    $('step1').hidden = false;
    await sleep(600);
    if (myRun !== runId) return;

    $('step2').hidden = false;
    await sleep(600);
    if (myRun !== runId) return;

    $('step3').hidden = false;
    await sleep(700);
    if (myRun !== runId) return;

    const outcome = await pending;
    if (myRun !== runId) return;

    button.disabled = false;
    if (!outcome.ok) {
        $('output').hidden = true;
        showErrors(outcome.errors);
        return;
    }

    $('step3').hidden = true;
    showResult(outcome.data);
}

// ------------------------------------------------------------
// Connect the buttons
// ------------------------------------------------------------
document.querySelectorAll('.tab').forEach((button) => {
    button.addEventListener('click', () => {
        state.type = button.dataset.type;
        updateForm();
    });
});

document.querySelectorAll('#find-chips .chip').forEach((button) => {
    button.addEventListener('click', () => {
        state.find = button.dataset.find;
        updateForm();
    });
});

document.querySelectorAll('#known-chips .chip').forEach((button) => {
    button.addEventListener('click', () => {
        state.known = button.dataset.known;
        updateForm();
    });
});

// Typing in a box makes the old answer out of date, so we clear it
$('calc-form').addEventListener('input', clearOutput);
$('calc-form').addEventListener('change', clearOutput);

$('calc-form').addEventListener('submit', (event) => {
    event.preventDefault();
    calculate();
});

updateForm();
