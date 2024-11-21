const $nav = document.getElementById('nav');
const $navItems = Array.from($nav.querySelectorAll(':scope > .menu > .item[rel]'));
const $main = document.getElementById('main');
const $mainContents = Array.from($main.querySelectorAll(':scope > .content[rel]'));

$navItems.forEach(($navItem) => {
    $navItem.onclick = () => {
        const rel = $navItem.getAttribute('rel');
        const $mainContent = $mainContents.find((x) => x.getAttribute('rel') === rel);
        $navItems.forEach((x) => x.classList.remove('-selected'));
        $navItem.classList.add('-selected');
        $mainContents.forEach((x) => x.hide());
        $mainContent.show();
    };
});
