const tabs = [document.querySelector(".doc-tab.lesson"), document.querySelector(".doc-tab.interview")];
const lists = [document.querySelector(".lesson-list"), document.querySelector(".interview-list")];

tabs.forEach((tab, index) => {
    let otherIndex = (index + 1) % 2;
    tab.addEventListener("click", () => {
        tab.classList.add("active")
        lists[index].classList.add("active");
        tabs[otherIndex].classList.remove("active")
        lists[otherIndex].classList.remove("active");
    });
})