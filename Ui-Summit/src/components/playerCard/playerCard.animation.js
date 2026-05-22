export const ANIMATION_DURATION = 300;

export function animateCardTransition(card, callback) {
    card.classList.remove('anim-enter');
    card.classList.add('anim-exit');

    setTimeout(() => {
        callback();
        card.classList.remove('anim-exit');
        card.classList.add('anim-enter');
    }, ANIMATION_DURATION);
}